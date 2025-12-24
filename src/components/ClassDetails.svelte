<script lang='ts'>
	import IconInput                   from './input/IconInput.svelte';
	import MaterialSelect              from './input/MaterialSelect.svelte';
	import SearchableSelect            from './input/SearchableSelect.svelte';
	import AttributeInput              from './input/AttributeInput.svelte';
	import ByteSelect                  from './input/ByteSelect.svelte';
	import { expSources }              from '../version/data';
	import { toProperCase }            from '$api/api';
	import { onDestroy, onMount, untrack }      from 'svelte';
	import ProInput                    from './input/ProInput.svelte';
	import Toggle                      from './input/Toggle.svelte';
	import LoreInput                   from '$input/LoreInput.svelte';
	import type { Unsubscriber }       from 'svelte/store';
	import FabledClass, { classStore } from '../data/class-store.svelte';
	import { skillStore }              from '../data/skill-store.svelte';
	import { attributeStore }          from '../data/attribute-store.js';

	interface Props {
		data: FabledClass;
		onsave?: () => void;
	}

	interface LoreVariable {
		index: number;      // 1-based position
		name: string;       // custom name (default: "Var X")
		value: string;      // current value (string to handle "50%", "1.5", etc.)
	}

	let { data = $bindable(), onsave }: Props = $props();

	let combosShown = $state(false);
	let translatedLoreShown = $state(false);
	let newLangCode = $state('');
	let sub: Unsubscriber;

	// Variable editing state
	let loreVariables: LoreVariable[] = $state([]);
	let editingVariable: LoreVariable | null = $state(null);
	let editVarName = $state('');
	let editVarValue = $state('');
	let showVarPopup = $state(false);

	const classes = classStore.classes;
	const skills  = skillStore.skills;

	// Regex to match numbers: integers, decimals, negatives, percentages
	const numberRegex = /-?\d+(?:\.\d+)?%?/g;

	// Extract all numbers from text
	const extractNumbers = (text: string): string[] => {
		return text.match(numberRegex) || [];
	};

	// Extract variables from the first language's lore
	const extractVariables = (): LoreVariable[] => {
		// Use untrack to prevent reactive dependency on translatedLore
		return untrack(() => {
			const langs = Object.keys(data.translatedLore);
			if (langs.length === 0) return [];

			const firstLang = langs[0];
			const loreLines = data.translatedLore[firstLang] || [];
			const fullText = loreLines.join('\n');
			const numbers = extractNumbers(fullText);

			return numbers.map((value, idx) => ({
				index: idx + 1,
				name: `Var ${idx + 1}`,
				value
			}));
		});
	};

	// Replace the Nth number occurrence in text with a new value
	const replaceNthNumber = (text: string, n: number, newValue: string): string => {
		let count = 0;
		return text.replace(numberRegex, (match) => {
			count++;
			return count === n ? newValue : match;
		});
	};

	// Update variable value across all languages
	const updateVariable = (varIndex: number, newValue: string) => {
		const langs = getLanguages();
		for (const lang of langs) {
			const loreLines = data.translatedLore[lang] || [];
			const fullText = loreLines.join('\n');
			const updatedText = replaceNthNumber(fullText, varIndex, newValue);
			data.translatedLore[lang] = updatedText.split('\n');
		}
		data.translatedLore = { ...data.translatedLore }; // Trigger reactivity
		refreshVariables();
	};

	// Refresh the variable list
	const refreshVariables = () => {
		const newVars = extractVariables();
		// Preserve custom names if they exist
		loreVariables = newVars.map(newVar => {
			const existing = loreVariables.find(v => v.index === newVar.index);
			return {
				...newVar,
				name: existing?.name && existing.name !== `Var ${existing.index}` ? existing.name : newVar.name
			};
		});
	};

	// Open popup to edit a variable
	const openVarEditor = (variable: LoreVariable) => {
		editingVariable = variable;
		editVarName = variable.name;
		editVarValue = variable.value;
		showVarPopup = true;
	};

	// Save variable changes from popup
	const saveVarChanges = () => {
		if (!editingVariable) return;

		// Update the name in our list
		const varInList = loreVariables.find(v => v.index === editingVariable!.index);
		if (varInList) {
			varInList.name = editVarName || `Var ${editingVariable.index}`;
		}

		// If value changed, update across all languages
		if (editVarValue !== editingVariable.value) {
			updateVariable(editingVariable.index, editVarValue);
		}

		closeVarEditor();
	};

	// Close popup
	const closeVarEditor = () => {
		showVarPopup = false;
		editingVariable = null;
		editVarName = '';
		editVarValue = '';
	};

	// Get available languages from translatedLore
	const getLanguages = () => Object.keys(data.translatedLore);

	// Add a new language
	const addLanguage = () => {
		if (newLangCode && !data.translatedLore[newLangCode]) {
			data.translatedLore[newLangCode] = [];
			newLangCode = '';
		}
	};

	// Remove a specific language
	const removeLanguage = (langCode: string) => {
		if (langCode && data.translatedLore[langCode]) {
			delete data.translatedLore[langCode];
			data.translatedLore = { ...data.translatedLore }; // Trigger reactivity
		}
	};

	// Track if we've already loaded variables for this session
	let varsLoaded = $state(false);

	// Only refresh variables ONCE when section is first opened
	$effect(() => {
		if (translatedLoreShown && !varsLoaded) {
			varsLoaded = true;
			// Use setTimeout to break out of the reactive tracking
			setTimeout(() => refreshVariables(), 0);
		}
		if (!translatedLoreShown) {
			varsLoaded = false;
		}
	});

	onMount(() => {
		sub = attributeStore.attributes.subscribe(value => {
			const included: string[] = [];
			data.attributes          = data.attributes.filter(a => {
				if (value.some((attr) => attr.name === a.name)) {
					included.push(a.name);
					return true;
				}
				return false;
			});

			for (const attrib of value.filter(attr => !included.includes(attr.name))) {
				data.attributes.push({ name: attrib.name, base: 0, scale: 0 });
			}
		});
	});

	onDestroy(() => {
		if (sub) sub();
	});

	$effect(() => onsave?.());
</script>

{#if data}
	<ProInput label='Name'
						tooltip='The name of the class. This should not contain color codes'
						bind:value={data.name} />
	<ProInput label='Prefix'
						tooltip='The prefix given to players who profess as the class which can contain color codes'
						bind:value={data.prefix} />
	<ProInput label='Action Bar'
						tooltip='The format for the action bar. Leave blank to use the default formatting'
						bind:value={data.actionBar} />
	<ProInput label='Group'
						tooltip='A class group are things such as "race", "class", and "trade". Different groups can be professed through at the same time, one class from each group'
						bind:value={data.group} />
	<ProInput label='Mana Name'
						tooltip='The name the class uses for mana'
						bind:value={data.manaName} />
	<ProInput label='Max Level'
						tooltip='The maximum level the class can reach. If this class turns into other classes, this will also be the level it can profess into those classes'
						bind:value={data.maxLevel} />
	<ProInput label='Parent'
						tooltip='The class that turns into this one. For example, if Fighter turns into Knight, then Knight would have its parent as Fighter'>
		<SearchableSelect id='parent'
											data={$classes.filter(c => c !== data)}
											bind:selected={data.parent} />
	</ProInput>
	<ProInput label='Permission'
						tooltip='Whether the class requires a permission to be professed as. The permission would be "fabled.class.{data.name.toLowerCase()}"'>
		<Toggle bind:data={data.permission} />
	</ProInput>
	<ProInput label='Exp Sources'
						tooltip='The experience sources the class goes up from. Most of these only work if "use-exp-orbs" is enabled in the config.yml'>
		<ByteSelect
			data={expSources}
			bind:value={data.expSources} />
	</ProInput>
	<ProInput label='Health'
						tooltip='The amount of health the class has'>
		<AttributeInput value={data.health} />
	</ProInput>
	<ProInput label='Mana'
						tooltip='The amount of mana the class has'>
		<AttributeInput value={data.mana} />
	</ProInput>

	<div class='info'>Drag & Drop your attributes file to use your custom attributes</div>
	{#each data.attributes as attr (attr.name)}
		<ProInput label={toProperCase(attr.name)}
							tooltip='The amount of {attr.name} the class has'>
			<AttributeInput value={attr} />
		</ProInput>
	{/each}

	<ProInput label='Mana Regen'
						tooltip='The amount of mana the class regenerates at each interval. The interval is in the config.yml and by default is once every second. If you want to regen a decimal amount per second, increase the interval'
						bind:value={data.manaRegen} />
	<ProInput label='Skill Tree'
						tooltip='The type of skill tree to use'>
		<select id='skill-tree' bind:value={data.skillTree}>
			<option value='Custom'>Custom</option>
			<option value='Requirement'>Requirement</option>
			<option value='Basic Horizontal'>Basic Horizontal</option>
			<option value='Basic Vertical'>Basic Vertical</option>
			<option value='Level Horizontal'>Level Horizontal</option>
			<option value='Level Vertical'>Level Vertical</option>
			<option value='Flood'>Flood</option>
		</select>
	</ProInput>

	<ProInput label='Skills'
						tooltip='The skills the class is able to use'>
		<SearchableSelect id='skills'
											data={$skills}
											multiple={true}
											bind:selected={data.skills}
											placeholder='No Skills' />
	</ProInput>

	<IconInput bind:icon={data.icon} />

	<ProInput label='Unusable items'
						tooltip='[blacklist] The types of items that the class cannot use'>
		<MaterialSelect multiple bind:selected={data.unusableItems} />
	</ProInput>

	<div class='header translated-lore'
			 role='button'
			 tabindex='0'
			 onclick={() => translatedLoreShown = !translatedLoreShown}
			 onkeypress={e => {
			 	if (e.key === 'Enter') translatedLoreShown = !translatedLoreShown;
			 }}>
		Translated Lore <span class='material-symbols-rounded'>{translatedLoreShown ? 'expand_less' : 'expand_more'}</span>
	</div>
	{#if translatedLoreShown}
		<div class='info'>Define multiple language versions of the class lore. The first language (zh-TW) is used as the default fallback.</div>
		
		<!-- Variable Numbers Section -->
		{#if loreVariables.length > 0}
			<div class='variables-section'>
				<div class='variables-header'>Variable Numbers</div>
				<div class='variables-info'>Click a variable to edit its value across all languages</div>
				<div class='variables-list'>
					{#each loreVariables as variable (variable.index)}
						<button 
							class='variable-chip'
							onclick={() => openVarEditor(variable)}
							title='Click to edit {variable.name}'>
							<span class='var-name'>{variable.name}</span>
							<span class='var-value'>{variable.value}</span>
						</button>
					{/each}
				</div>
			</div>
		{/if}

		{#each getLanguages() as lang (lang)}
			<ProInput label='Lore ({lang})'
								tooltip='The lore text for {lang} (one line per row)'>
				<div class='lore-with-delete'>
					<LoreInput bind:value={data.translatedLore[lang]} />
					<button class='remove-lang-btn' onclick={() => removeLanguage(lang)} title='Remove {lang}'>
						<span class='material-symbols-rounded'>delete</span>
					</button>
				</div>
			</ProInput>
		{/each}
		<ProInput label='Add Language'
							tooltip='Add a new language code (e.g., en-US, zh-CN)'>
			<div class='lang-controls'>
				<input type='text' bind:value={newLangCode} placeholder='e.g., en-US' />
				<button class='add-btn' onclick={addLanguage} title='Add language'>
					<span class='material-symbols-rounded'>add</span>
				</button>
			</div>
		</ProInput>

		<!-- Refresh Variables Button -->
		<div class='refresh-vars-container'>
			<button class='refresh-vars-btn' onclick={refreshVariables} title='Refresh variable list from lore'>
				<span class='material-symbols-rounded'>refresh</span> Refresh Variables
			</button>
		</div>
	{/if}

	<!-- Variable Edit Popup -->
	{#if showVarPopup && editingVariable}
		<div class='popup-overlay' onclick={closeVarEditor} role='presentation'>
			<div class='popup-content' onclick={(e) => e.stopPropagation()} role='dialog'>
				<div class='popup-header'>
					<span>Edit Variable</span>
					<button class='popup-close' onclick={closeVarEditor}>
						<span class='material-symbols-rounded'>close</span>
					</button>
				</div>
				<div class='popup-body'>
					<label>
						<span>Variable Name</span>
						<input type='text' bind:value={editVarName} placeholder='e.g., Damage, Cooldown' />
					</label>
					<label>
						<span>Value</span>
						<input type='text' bind:value={editVarValue} placeholder='e.g., 100, 50%, 1.5' />
					</label>
					<div class='popup-hint'>
						Changing the value will replace the {editingVariable.index}{editingVariable.index === 1 ? 'st' : editingVariable.index === 2 ? 'nd' : editingVariable.index === 3 ? 'rd' : 'th'} number in all languages.
					</div>
				</div>
				<div class='popup-footer'>
					<button class='popup-cancel' onclick={closeVarEditor}>Cancel</button>
					<button class='popup-save' onclick={saveVarChanges}>Save</button>
				</div>
			</div>
		</div>
	{/if}

	<div class='header combos'
			 role='button'
			 tabindex='0'
			 onclick={() => combosShown = !combosShown}
			 onkeypress={e => {
			 	if (e.key === 'Enter') combosShown = !combosShown;
			 }}>
		Combo Starters <span class='material-symbols-rounded'>{combosShown ? 'expand_less' : 'expand_more'}</span>
	</div>
	{#if combosShown}
		<div class='info'>These are the materials that can be used as combo starters. If a material is not in the list, it
			cannot be used as a combo starter. If the list is inverted, then the materials in the list cannot be used as combo
			starters
		</div>
		<ProInput label='L Inverted'
							tooltip='Whether the L list should be used as a blacklist'>
			<Toggle bind:data={data.lInverted} />
		</ProInput>
		<ProInput label='L Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.lWhitelist} />
		</ProInput>
		<ProInput label='R Inverted'
							tooltip='Whether the R list should be used as a blacklist'>
			<Toggle bind:data={data.rInverted} />
		</ProInput>
		<ProInput label='R Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rWhitelist} />
		</ProInput>
		<ProInput label='LS Inverted'
							tooltip='Whether the LS list should be used as a blacklist'>
			<Toggle bind:data={data.lsInverted} />
		</ProInput>
		<ProInput label='LS Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.lsWhitelist} />
		</ProInput>
		<ProInput label='RS Inverted'
							tooltip='Whether the RS list should be used as a blacklist'>
			<Toggle bind:data={data.rsInverted} />
		</ProInput>
		<ProInput label='RS Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rsWhitelist} />
		</ProInput>
		<ProInput label='S Inverted'
							tooltip='Whether the S list should be used as a blacklist'>
			<Toggle bind:data={data.rsInverted} />
		</ProInput>
		<ProInput label='S Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rsWhitelist} />
		</ProInput>
		<ProInput label='P Inverted'
							tooltip='Whether the P list should be used as a blacklist'>
			<Toggle bind:data={data.pInverted} />
		</ProInput>
		<ProInput label='P Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.pWhitelist} />
		</ProInput>
		<ProInput label='Q Inverted'
							tooltip='Whether the Q list should be used as a blacklist'>
			<Toggle bind:data={data.qInverted} />
		</ProInput>
		<ProInput label='Q Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.qWhitelist} />
		</ProInput>
		<ProInput label='F Inverted'
							tooltip='Whether the F list should be used as a blacklist'>
			<Toggle bind:data={data.fInverted} />
		</ProInput>
		<ProInput label='F Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.fWhitelist} />
		</ProInput>
	{/if}
{/if}

<style>
    .info {
        grid-column: 1 / span 2;
        text-align: center;
        margin-left: 5rem;
        color: rgba(255, 255, 255, 0.7);
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    .header {
        grid-column: 1 / -1;
        font-size: 1.2em;
        font-weight: bold;
        text-align: center;
        padding-bottom: 1rem;
    }

    .header::before {
        content: ' ';
        display: block;
        width: 40%;
        height: 1px;
        background: white;
        margin: 1rem auto;
    }

    .combos, .translated-lore {
        cursor: pointer;
    }

    .lang-controls {
        display: flex;
        gap: 0.5rem;
        align-items: center;
        width: 100%;
    }

    .lang-controls input {
        flex: 1;
    }

    .lang-controls button {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0.25rem 0.5rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .add-btn {
        background-color: #4caf50;
        color: white;
    }

    .add-btn:hover {
        background-color: #45a049;
    }

    .lore-with-delete {
        display: flex;
        gap: 0.5rem;
        align-items: flex-start;
        width: 100%;
    }

    .lore-with-delete :global(textarea) {
        flex: 1;
    }

    .remove-lang-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0.25rem 0.5rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s;
        background-color: #f44336;
        color: white;
        height: fit-content;
        margin-top: 0.25rem;
    }

    .remove-lang-btn:hover {
        background-color: #da190b;
    }

    .lang-controls .material-symbols-rounded,
    .remove-lang-btn .material-symbols-rounded {
        font-size: 1.2rem;
    }

    /* Variables Section Styles */
    .variables-section {
        grid-column: 1 / -1;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 8px;
        padding: 1rem;
        margin-bottom: 1rem;
    }

    .variables-header {
        font-size: 1.1em;
        font-weight: bold;
        margin-bottom: 0.25rem;
        color: #fff;
    }

    .variables-info {
        font-size: 0.85em;
        color: rgba(255, 255, 255, 0.6);
        margin-bottom: 0.75rem;
    }

    .variables-list {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .variable-chip {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.4rem 0.75rem;
        background: rgba(100, 150, 255, 0.2);
        border: 1px solid rgba(100, 150, 255, 0.4);
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.2s;
        color: #fff;
    }

    .variable-chip:hover {
        background: rgba(100, 150, 255, 0.35);
        border-color: rgba(100, 150, 255, 0.6);
        transform: translateY(-1px);
    }

    .var-name {
        font-size: 0.85em;
        color: rgba(255, 255, 255, 0.8);
    }

    .var-value {
        font-weight: bold;
        color: #7cb3ff;
    }

    .refresh-vars-container {
        grid-column: 1 / -1;
        display: flex;
        justify-content: center;
        margin-top: 0.5rem;
    }

    .refresh-vars-btn {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 4px;
        color: rgba(255, 255, 255, 0.8);
        cursor: pointer;
        transition: all 0.2s;
    }

    .refresh-vars-btn:hover {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
    }

    /* Popup Styles */
    .popup-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
    }

    .popup-content {
        background: #2a2a3e;
        border-radius: 12px;
        width: 90%;
        max-width: 400px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
        overflow: hidden;
    }

    .popup-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 1.25rem;
        background: rgba(255, 255, 255, 0.05);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        font-weight: bold;
        font-size: 1.1em;
    }

    .popup-close {
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.6);
        cursor: pointer;
        padding: 0.25rem;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        transition: all 0.2s;
    }

    .popup-close:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #fff;
    }

    .popup-body {
        padding: 1.25rem;
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .popup-body label {
        display: flex;
        flex-direction: column;
        gap: 0.4rem;
    }

    .popup-body label span {
        font-size: 0.9em;
        color: rgba(255, 255, 255, 0.7);
    }

    .popup-body input {
        padding: 0.6rem 0.75rem;
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 6px;
        background: rgba(255, 255, 255, 0.05);
        color: #fff;
        font-size: 1em;
    }

    .popup-body input:focus {
        outline: none;
        border-color: #7cb3ff;
    }

    .popup-hint {
        font-size: 0.85em;
        color: rgba(255, 255, 255, 0.5);
        background: rgba(255, 255, 255, 0.05);
        padding: 0.75rem;
        border-radius: 6px;
    }

    .popup-footer {
        display: flex;
        justify-content: flex-end;
        gap: 0.75rem;
        padding: 1rem 1.25rem;
        background: rgba(255, 255, 255, 0.03);
        border-top: 1px solid rgba(255, 255, 255, 0.1);
    }

    .popup-cancel, .popup-save {
        padding: 0.5rem 1.25rem;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.95em;
        transition: all 0.2s;
    }

    .popup-cancel {
        background: rgba(255, 255, 255, 0.1);
        color: rgba(255, 255, 255, 0.8);
    }

    .popup-cancel:hover {
        background: rgba(255, 255, 255, 0.2);
    }

    .popup-save {
        background: #4caf50;
        color: white;
    }

    .popup-save:hover {
        background: #45a049;
    }
</style>