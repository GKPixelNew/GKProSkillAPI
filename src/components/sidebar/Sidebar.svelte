<!--suppress CssUnresolvedCustomProperty -->
<script lang='ts'>
	import { closeSidebar, shownTab, sidebarOpen } from '../../data/store';
	import SidebarEntry                            from './SidebarEntry.svelte';
	import { squish }                              from '../../data/squish';
	import { goto }                                from '$app/navigation';
	import { beforeUpdate, onDestroy, onMount }    from 'svelte';
	import type { Unsubscriber }                   from 'svelte/store';
	import { get }                                 from 'svelte/store';
	import Folder                                  from '../Folder.svelte';
	import { fly }                                 from 'svelte/transition';
	import { clickOutside }                        from '$api/clickoutside';
	import { browser }                             from '$app/environment';
	import Tabs                                    from '../input/Tabs.svelte';
	import { base }                                from '$app/paths';
	import { socketService }                       from '$api/socket/socket-connector';
	import { Tab }                                 from '$api/tab';
	import FabledSkill, { skillStore }             from '../../data/skill-store.js';
	import type FabledAttribute                    from '$api/fabled-attribute';
	import FabledClass, { classStore }             from '../../data/class-store';
	import { attributeStore }                      from '../../data/attribute-store.js';
	import { FabledFolder }                        from '../../data/folder-store';
	import { getAllClasses, getAllSkills, reloadAllClasses, reloadAllSkills } from '$api/cdn';
	import Modal from "$components/Modal.svelte";
	import ProInput from "$input/ProInput.svelte";
	import {Circle} from "svelte-loading-spinners";
	import type FabledAttribute from '$api/fabled-attribute';
	import { importClass, importSkill } from '$api/cdn.js';

	let folders: FabledFolder[] = [];
	let classSub: Unsubscriber;
	let skillSub: Unsubscriber;
	let classIncluded: Array<FabledClass | FabledSkill> = [];
	let skillIncluded: Array<FabledClass | FabledSkill> = [];

	let width: number;
	let height: number;
	let scrollY: number;

	let importing: 'class' | 'skill' | 'none' = 'none';
	let options = {
		classes: [],
		skills: []
	};
	let importChoice = '';
	let loadingOptions = false;
	const appendIncluded = (item: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute> | FabledFolder | FabledClass | FabledSkill | FabledAttribute, include: Array<FabledClass | FabledSkill | FabledAttribute>) => {

	const skills       = skillStore.skills;
	const skillFolders = skillStore.skillFolders;
	const classes      = classStore.classes;
	const classFolders = classStore.classFolders;
	const attributes   = attributeStore.attributes;

	const appendIncluded = (item: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute> | FabledFolder | FabledClass | FabledSkill | FabledAttribute, include: Array<FabledClass | FabledSkill>) => {
		if (item instanceof Array) item.forEach(fold => appendIncluded(fold, include));
		if (item instanceof FabledFolder) appendIncluded(item.data, include);
		else if (item instanceof FabledClass || item instanceof FabledSkill) include.push(item);
	};

	const rebuildFolders = (fold?: FabledFolder[]) => {
		switch (get(shownTab)) {
			case Tab.CLASSES: {
				folders = fold || get(classFolders);
				classIncluded = [];
				appendIncluded(folders, classIncluded);
				break;
			}
			case Tab.SKILLS: {
				folders       = fold || get(skillStore.skillFolders);
				skillIncluded = [];
				appendIncluded(folders, skillIncluded);
				break;
			}
		}
	};

	const onImportClass = () => {
		importing = 'class';
		loadingOptions = true;
		getAllClasses().then(res => {
			options.classes = res;
		}).finally(() => {
			loadingOptions = false;
		});
	};

	const onImportSkill = () => {
		importing = 'skill';
		loadingOptions = true;
		getAllSkills().then(res => {
			options.skills = res;

		}).finally(() => {
			loadingOptions = false;
		});
	};

	onMount(() => {
		if (!browser) return;

		classSub = classFolders.subscribe(rebuildFolders);
		skillSub = skillStore.skillFolders.subscribe(rebuildFolders);
	});

	beforeUpdate(rebuildFolders);

	onDestroy(() => {
		if (classSub) classSub();
		if (skillSub) skillSub();
	});

	const clickOut = (e: MouseEvent) => {
		if (width < 500) {
			e.stopPropagation();
			closeSidebar();
		}
	};

	const closeModal = () => {
		importing = 'none';
	};
</script>

<svelte:window bind:innerWidth={width} bind:innerHeight={height} bind:scrollY={scrollY}
							 on:beforeunload={() => socketService.disconnect()} />

<div id='sidebar'
		 on:introend={() => sidebarOpen.set(true)}
		 on:outroend={() => sidebarOpen.set(false)}
		 style:--height='calc({height}px - 6rem + min(3rem, {scrollY}px))'
		 transition:squish
		 use:clickOutside={clickOut}>
	<div class='type-wrap'>
		<Tabs bind:selectedTab={$shownTab} color='#111' data={["Classes", "Skills", "Attributes"]} inline={false} />
		<hr />
	</div>
	{#if $shownTab === Tab.CLASSES}
		<div class='items'
				 in:fly={{x: -100}}
				 out:fly={{x: -100}}>
			{#each $classFolders as cf}
				<Folder folder={cf} />
			{/each}
			{#each $classes.filter(c => !classIncluded.includes(c)) as cl, i (cl.key)}
				<SidebarEntry
					data={cl}
					delay={200 + 100*i}
					on:click={() => goto(`${base}/class/${cl.name}/edit`)}>
					{cl.name}{cl.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($classes.length+1)}>
				<div class='new'>
					<span tabindex='0'
								role='button'
								on:click={() => classStore.addClass()}
								on:keypress={(e) => e.key === 'Enter' && classStore.addClass()}>New Class</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								on:click={() => classStore.addClassFolder(new FabledFolder())}
								on:keypress={(e) => e.key === 'Enter' && classStore.addClassFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
			<SidebarEntry delay={200 + 100*($classes.length+2)}>
				<div class='new'>
					<span tabindex='0'
								role='button'
								style="font-size: 14.9px"
								on:click={() => onImportClass()}
								on:keypress={(e) => e.key === 'Enter' && onImportClass()}>匯入信仰</span>
					<span tabindex='0'
								role='button'
								on:click={() => reloadAllClasses()}
								on:keypress={(e) => e.key === 'Enter' && reloadAllClasses()}>重新整理</span>
				</div>
			</SidebarEntry>
		</div>
	{:else if $shownTab === Tab.SKILLS}
		<div class='items'
				 in:fly={{ x: 100 }}
				 out:fly={{ x: 100 }}>
			{#each $skillFolders as sk}
				<Folder folder={sk} />
			{/each}
			{#each $skills.filter(s => !skillIncluded.includes(s)) as sk, i (sk.key)}
				<SidebarEntry
					data={sk}
					direction='right'
					delay={200 + 100*i}
					on:click={() => goto(`${base}/skill/${sk.name}`)}>
					{sk.name}{sk.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($skills.length+1)}
				direction='right'>
				<div class='new'>
					<span tabindex='0'
								role='button'
								on:click={() => skillStore.addSkill()}
								on:keypress={(e) => e.key === 'Enter' && skillStore.addSkill()}>New Skill</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								on:click={() => skillStore.addSkillFolder(new FabledFolder())}
								on:keypress={(e) => e.key === 'Enter' && skillStore.addSkillFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
			<SidebarEntry delay={200 + 100*($classes.length+2)} direction="right">
				<div class='new'>
						<span tabindex='0'
									role='button'
									on:click={() => onImportSkill()}
									on:keypress={(e) => e.key === 'Enter' && onImportSkill()}>匯入技能</span>
					<span tabindex='0'
								role='button'
								on:click={() => reloadAllSkills()}
								on:keypress={(e) => e.key === 'Enter' && reloadAllSkills()}>重新整理</span>
				</div>
			</SidebarEntry>
		</div>
	{:else if $shownTab === Tab.ATTRIBUTES}
		<div class='items'
				 in:fly={{ x: 100 }}
				 out:fly={{ x: 100 }}>
			{#each $attributes as att, i (att.name)}
				<SidebarEntry
					data={att}
					direction='right'
					delay={200 + 100*i}
					on:click={() => goto(`${base}/attribute/${att.name}/edit`)}>
					{att.name}{att.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($attributes.length+1)}
				direction='right'>
				<div class='new'>
					<span tabindex='0'
								role='button'
								on:click={() => attributeStore.addAttribute()}
								on:keypress={(e) => e.key === 'Enter' && attributeStore.addAttribute()}>New Attribute</span>
				</div>
			</SidebarEntry>
		</div>
	{/if}
</div>

<Modal on:close={closeModal} open={importing === "class"} width="300px">
	<h2>匯入信仰</h2>
	<hr />
	<div class='import-container'>
		<ProInput label='信仰 ID' tooltip='要匯入的信仰 ID'>
			{#if loadingOptions}
				<Circle size="25" color="#0083ef" />
			{:else}
				<select bind:value={importChoice}>
					{#each options.classes as opt}
						<option value={opt}>{opt}</option>
					{/each}
				</select>
			{/if}
		</ProInput>
		<button on:click={() => importClass(importChoice)}
						on:keypress={(e) => e.key === 'Enter' && importClass(importChoice)}
						class="button"
						style="grid-column: 1 / span 2"
						tabindex='0'>確認匯入</button>
	</div>
</Modal>

<Modal on:close={closeModal} open={importing === "skill"} width="500px">
	<h2>匯入技能</h2>
	<hr />
	<div class='import-container'>
		<ProInput label='技能 ID' tooltip='要匯入的技能 ID'>
			{#if loadingOptions}
				<Circle size="25" color="#0083ef" />
			{:else}
				<select bind:value={importChoice}>
					{#each options.skills as opt}
						<option value={opt}>{opt}</option>
					{/each}
				</select>
			{/if}
		</ProInput>
		<button on:click={() => importSkill(importChoice)}
					on:keypress={(e) => e.key === 'Enter' && importSkill(importChoice)}
						class="button"
						style="grid-column: 1 / span 2"
					tabindex='0'>確認匯入</button>
	</div>
</Modal>

<style>
    .import-container {
        display: grid;
        grid-template-columns: 50% 50%;
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    #sidebar {
        position: absolute;
        top: 0;
        left: 0;
        z-index: 30;
        background-color: #222;
        max-height: var(--height);
        height: var(--height);
        overflow-y: auto;
        width: 75%;
    }

    hr {
        margin-bottom: 0;
    }

    .type-wrap {
        position: sticky;
        z-index: 2;
        top: 0;
        background-color: #222;
        padding: 0.4rem;
        user-select: none;
        -webkit-user-select: none;
    }

    .items {
        position: absolute;
        width: 100%;
    }

    .new {
        width: 100%;
        display: flex;
        justify-content: space-around;
        margin: 0.3rem;
    }

    .new span {
        display: grid;
        place-items: center;
        flex: 1;
        border-radius: 100vw;
        text-align: center;
        padding: 0.4rem 0.6rem;
        background-color: #333;
    }

    .new span:first-child {
        margin-right: 0.5rem;
    }

    .new span:last-child {
        margin-left: 0.5rem;
    }

    .new span:hover {
        background-color: #0083ef;
    }

    @media screen and (min-width: 500px) {
        #sidebar {
            position: sticky;
            top: 3rem;
            width: 15rem;
            min-width: 10rem;
            overflow-x: hidden;
            overflow-y: auto;
        }
    }
</style>