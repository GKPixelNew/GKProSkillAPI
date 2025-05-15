<script lang='ts'>
	import { closeSidebar, shownTab, sidebarOpen } from '../../data/store';
	import SidebarEntry                            from './SidebarEntry.svelte';
	import { squish }                              from '../../data/squish';
	import { goto }                                from '$app/navigation';
	import { onDestroy, onMount }                  from 'svelte';
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
	import FabledSkill, { skillStore }             from '../../data/skill-store.svelte';
	import FabledClass, { classStore }             from '../../data/class-store.svelte';
	import { attributeStore }                      from '../../data/attribute-store';
	import { FabledFolder }                        from '../../data/folder-store.svelte';
	import { sort }                                from '$api/api';
	import { getAllClasses, getAllSkills, reloadAllClasses, reloadAllSkills } from '$api/cdn';
	import Modal from "$components/Modal.svelte";
	import ProInput from "$input/ProInput.svelte";
	import {Circle} from "svelte-loading-spinners";
	import { importClass, importSkill } from '$api/cdn.js';

	let folders: FabledFolder[] = [];
	let tabSub: Unsubscriber;
	let classSub: Unsubscriber;
	let skillSub: Unsubscriber;
	let classIncluded: Array<FabledClass | FabledSkill> = $state([]);
	let skillIncluded: Array<FabledClass | FabledSkill> = $state([]);

	let built = $state(false);

	let width: number   = $state(0);
	let height: number  = $state(0);
	let scrollY: number = $state(0);

	let importing: 'class' | 'skill' | 'none' = 'none';
	let options = {
		classes: [],
		skills: []
	};
	let importChoice = '';
	let loadingOptions = false;
	const skills       = skillStore.skills;
	const skillFolders = skillStore.skillFolders;
	const classes      = classStore.classes;
	const classFolders = classStore.classFolders;
	const attributes   = attributeStore.attributes;

	const rebuildFolders = (fold?: FabledFolder[]) => {
		switch (get(shownTab)) {
			case Tab.CLASSES: {
				folders = fold || get(classFolders);
				classIncluded = [];
				for (let folder of folders) {
					classIncluded.push(...folder.getAllClasses());
				}
				break;
			}
			case Tab.SKILLS: {
				folders       = fold || get(skillFolders);
				skillIncluded = [];
				for (let folder of folders) {
					skillIncluded.push(...folder.getAllSkills());
				}
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

		tabSub   = shownTab.subscribe(() => rebuildFolders());
		classSub = classFolders.subscribe(rebuildFolders);
		skillSub = skillFolders.subscribe(rebuildFolders);
		rebuildFolders();
		built = true;
	});

	onDestroy(() => {
		if (tabSub) tabSub();
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

<svelte:window
	bind:innerHeight={height}
	bind:innerWidth={width}
	bind:scrollY
	onbeforeunload={() => socketService.disconnect()}
/>

<div id='sidebar'
		 onintroend={() => sidebarOpen.set(true)}
		 onoutroend={() => sidebarOpen.set(false)}
		 style:--height='100%'
		 transition:squish
		 use:clickOutside={clickOut}>
	<div class='type-wrap'>
		<Tabs
			bind:selectedTab={$shownTab}
			color='#111' data={['Classes', 'Skills', 'Attributes']}
			inline={false} />
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
					onclick={() => goto(`${base}/class/${cl.name}/edit`)}>
					{cl.name}{cl.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($classes.length+1)}>
				<div class='new'>
					<span tabindex='0'
								role='button'
								onclick={() => classStore.addClass()}
								onkeypress={(e) => e.key === 'Enter' && classStore.addClass()}>New Class</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								onclick={() => classStore.addClassFolder(new FabledFolder())}
								onkeypress={(e) => e.key === 'Enter' && classStore.addClassFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
			<SidebarEntry delay={200 + 100*($classes.length+2)}>
				<div class='new'>
					<span tabindex='0'
								role='button'
								style="font-size: 14.9px"
								onclick={() => onImportClass()}
								onkeypress={(e) => e.key === 'Enter' && onImportClass()}>匯入信仰</span>
					<span tabindex='0'
								role='button'
								onclick={() => reloadAllClasses()}
								onkeypress={(e) => e.key === 'Enter' && reloadAllClasses()}>重新整理</span>
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
					onclick={() => goto(`${base}/skill/${sk.name}`)}>
					{sk.name}{sk.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($skills.length+1)}
				direction='right'>
				<div class='new'>
					<span tabindex='0'
								role='button'
								onclick={() => skillStore.addSkill()}
								onkeypress={(e) => e.key === 'Enter' && skillStore.addSkill()}>New Skill</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								onclick={() => skillStore.addSkillFolder(new FabledFolder())}
								onkeypress={(e) => e.key === 'Enter' && skillStore.addSkillFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
			<SidebarEntry delay={200 + 100*($classes.length+2)} direction="right">
				<div class='new'>
						<span tabindex='0'
									role='button'
									onclick={() => onImportSkill()}
									onkeypress={(e) => e.key === 'Enter' && onImportSkill()}>匯入技能</span>
					<span tabindex='0'
								role='button'
								onclick={() => reloadAllSkills()}
								onkeypress={(e) => e.key === 'Enter' && reloadAllSkills()}>重新整理</span>
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
					onclick={() => goto(`${base}/attribute/${att.name}/edit`)}>
					{att.name}{att.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($attributes.length+1)}
				direction='right'>
				<div class='new'>
					<span tabindex='0'
								role='button'
								onclick={() => attributeStore.addAttribute()}
								onkeypress={(e) => e.key === 'Enter' && attributeStore.addAttribute()}>New Attribute</span>
				</div>
			</SidebarEntry>
		</div>
	{/if}
</div>

{#if importing === "class"}
<Modal onclose={closeModal} width="300px">
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
		<button onclick={() => importClass(importChoice)}
						onkeypress={(e) => e.key === 'Enter' && importClass(importChoice)}
						class="button"
						style="grid-column: 1 / span 2"
						tabindex='0'>確認匯入</button>
	</div>
</Modal>
{/if}
{#if importing === "skill"}
<Modal onclose={closeModal} width="500px">
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
		<button onclick={() => importSkill(importChoice)}
					onkeypress={(e) => e.key === 'Enter' && importSkill(importChoice)}
						class="button"
						style="grid-column: 1 / span 2"
					tabindex='0'>確認匯入</button>
	</div>
</Modal>
{/if}

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
