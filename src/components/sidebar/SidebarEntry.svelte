<script lang='ts'>
	import { active, deleteProData, dragging, isSyncLocal, localSyncList, saveData, shownTab, sidebarOpen, toggleSyncLocal } from '../../data/store';
	import { browser } 															from '$app/environment';
	import FabledAttribute                                                      from '$api/fabled-attribute.svelte';
	import { get }                                                              from 'svelte/store';
	import { fly, type TransitionConfig }                                       from 'svelte/transition';
	import Modal                                                                from '../Modal.svelte';
	import { animationEnabled }                                                 from '../../data/settings';
	import { base }                                                             from '$app/paths';
	import { Tab }                                                              from '$api/tab';
	import FabledSkill, { skillStore }                                          from '../../data/skill-store.svelte';
	import FabledClass, { classStore }                                          from '../../data/class-store.svelte';
	import { FabledFolder, folderStore }                                        from '../../data/folder-store.svelte.js';
	import { attributeStore }                                                   from '../../data/attribute-store';
	import { importSkill, importClass, reloadAllSkills, reloadAllAttributes } from '$api/cdn';
	import { pendingUpdates, type UpdateEvent } from '$api/sse';


	interface Props {
		delay?: number;
		direction?: 'right' | 'left';
		data?: FabledSkill | FabledClass | FabledAttribute | undefined;
		children?: import('svelte').Snippet;

		onclick?: (e: MouseEvent | KeyboardEvent) => void;
	}

	let {
				delay     = 0,
				direction = 'left',
				data      = undefined,
				children,
				onclick
			}: Props = $props();

	let over     = $state(false);
	let deleting = $state(false);
	let sync     = $state(data ? isSyncLocal(data) : false);

	// Check if this item has pending updates
	const getUpdateKey = () => {
		if (!data) return null;
		if (data instanceof FabledSkill) return `skill:${data.name}`;
		if (data instanceof FabledClass) return `class:${data.name}`;
		if (data instanceof FabledAttribute) return `attribute:all`;
		return null;
	};
	
	let hasPendingUpdate = $derived.by(() => {
		const key = getUpdateKey();
		if (!key) return false;
		return $pendingUpdates.has(key);
	});
	
	let pendingUpdateInfo = $derived.by(() => {
		const key = getUpdateKey();
		if (!key) return null;
		return $pendingUpdates.get(key);
	});

	const startDrag = (e: DragEvent) => {
		if (!data) {
			e.preventDefault();
			return;
		}
		dragging.set(data);
	};

	const drop = (e: Event) => {
		e.stopPropagation();
		e.preventDefault();
		const dragData: FabledClass | FabledSkill | FabledAttribute | FabledFolder = get(dragging);
		let targetFolder;
		if (data) {
			targetFolder = folderStore.getFolder(data);
		}

		const containing = folderStore.getFolder(dragData);
		if (containing) containing.remove(dragData);
		if (targetFolder) {
			targetFolder.add(dragData);
			over = false;
			folderStore.updateFolders();
			return;
		}
		if (dragData instanceof FabledFolder) {
			switch (get(shownTab)) {
				case Tab.CLASSES: {
					classStore.addClassFolder(dragData);
					break;
				}
				case Tab.SKILLS: {
					skillStore.addSkillFolder(dragData);
					break;
				}
			}
			dragData.parent = undefined;
		}

		over = false;
	};

	const dragOver = (e: Event) => {
		e.preventDefault();
		const dragData = get(dragging);
		if (data === dragData) return;
		over = true;
	};


	const maybe = (node: Element, options: {
		fn: (node: Element, options: object) => TransitionConfig
	} & TransitionConfig & { x?: number }) => {
		if (!get(animationEnabled)) {
			options.delay = 0;
		}
		return options.fn(node, options);
	};

	const cloneData = (data?: FabledClass | FabledSkill | FabledAttribute, e?: Event) => {
		e?.preventDefault();
		e?.stopPropagation();

		if (!data) return;

		if (data instanceof FabledClass) {
			classStore.cloneClass(data);
		} else if (data instanceof FabledSkill) {
			skillStore.cloneSkill(data);
		} else if (data instanceof FabledAttribute) {
			attributeStore.cloneAttribute(data);
		}
	};
</script>


<div class='sidebar-entry'
		 class:active={data && $active === data}
		 class:in-folder={!!folderStore.getFolder(data)}
		 class:over
		 class:activeSync={sync}
		 class:hasUpdate={hasPendingUpdate}
		 draggable='{!!data}'
		 in:maybe={{fn: fly, x: (direction === "left" ? -100 : 100), duration: 500, delay: $sidebarOpen ? 0 : delay}}
		 {onclick}
		 ondragleave={() => over = false}
		 ondragover={dragOver}
		 ondragstart={startDrag}
		 ondrop={drop}
		 onkeypress={(e) => {
			 if (e.key === 'Enter') onclick?.(e);
		 }}
		 out:fly={{x: (direction === "left" ? -100 : 100), duration: 500}}
		 role='menuitem'
		 tabindex='0'>
	<span class="entry-content" class:hasUpdate={hasPendingUpdate}>
		{#if hasPendingUpdate}
			<span class="update-icon material-symbols-rounded" title="有新版本可用 (被 {pendingUpdateInfo?.uploadedBy} 更新)">download</span>
		{/if}
		{@render children?.()}
	</span>
	{#if data}
		<div class='buttons'>
			{#if data instanceof FabledSkill}
				<a href='{base}/skill/{data.name}/edit'
					 class='edit'
					 title='Edit Skill'>
          <span class='material-symbols-rounded'>
            edit
          </span>
				</a>
			{/if}
			
			{#if !browser || ('showOpenFilePicker' in window)}
			{#key sync}
			<div onclick={(e) => {
				if (data instanceof FabledSkill) {
					importSkill(data.name);
				} else if (data instanceof FabledClass) {
					importClass(data.name);
				} else if (data instanceof FabledAttribute) {
					reloadAllAttributes();
				}
			}}
				onkeypress={(event) => {
					if (event?.key === 'Enter') {
						if (data instanceof FabledSkill) {
							importSkill(data.name);
						} else if (data instanceof FabledClass) {
							importClass(data.name);
						} else if (data instanceof FabledAttribute) {
							reloadAllAttributes();
						}
					}
				}}
				class:activeSync={sync}
				class:hasUpdate={hasPendingUpdate}
				tabindex='0'
				role='button'
				class='sync'
				title={hasPendingUpdate 
					? `有更新！被 ${pendingUpdateInfo?.uploadedBy} 修改` 
					: (data instanceof FabledAttribute ? '重新載入所有屬性' : '重新整理')}>
			   <span class='material-symbols-rounded'>{hasPendingUpdate ? 'sync_problem' : 'sync'}</span>
			</div>
			{/key}
			{/if}

			<div onclick={(e) => saveData(data, e)}
					 onkeypress={(event) => {if (event?.key === 'Enter') saveData(data, event);}}
					 tabindex='0'
					 role='button'
					 class='download'
					 title='Save {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          save
        </span>
			</div>
			<div onclick={(e) => cloneData(data, e)}
					 onkeypress={(event) => { if (event?.key === 'Enter') cloneData(data, event); }}
					 tabindex='0'
					 role='button'
					 class='clone'
					 title='Clone {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          content_copy
        </span>
			</div>
			<div onclick={(event) => {
				event.stopPropagation();
				event.preventDefault();
						// If holding shift, delete without confirmation
						if (event?.shiftKey) {
							deleteProData(data);
							return;
						}
						deleting = true
					}}
					 onkeypress={(event) => {
						 if (event?.key === 'Enter') {
							 event.stopPropagation();
							 event.preventDefault();
							 // If holding shift, delete without confirmation
							 if (event?.shiftKey) {
								 deleteProData(data);
								 return;
							 }
							 deleting = true;
						 }
					 }}
					 tabindex='0'
					 role='button'
					 class='delete'
					 title='Delete {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          delete
        </span>
			</div>
		</div>
	{/if}
</div>

{#if deleting}
	<Modal>
		<h3>Do you really want to delete {data?.name}?</h3>
		<div class='modal-buttons'>
			<div class='button' onclick={() => deleting = false}
					 onkeypress={(event) => { if (event?.key === 'Enter') deleting = false; }}
					 role='button'
					 tabindex='0'
			>Cancel
			</div>
			<div class='button modal-delete' onclick={() => deleteProData(data)}
					 onkeypress={(event) => { if (event?.key === 'Enter') deleteProData(data); }}
					 role='button'
					 tabindex='0'
			>Delete
			</div>
		</div>
	</Modal>
{/if}

<style>
    .sidebar-entry {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #444;
        padding: 0.3rem 0.5rem;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: break-spaces;
        border-left: 0 solid var(--color-accent);
        transition: background-color 0.25s ease-in-out,
        border-left-width 0.25s ease-in-out;
        user-select: none;
        -webkit-user-select: none;
        margin-inline: 0.4rem;
    }

    .sidebar-entry:hover {
        cursor: pointer;
    }

    .sidebar-entry:not(.in-folder):has(.new) {
        position: sticky;
        margin-top: 0.5rem;
        bottom: 0;
        background-color: unset;
    }

    .sidebar-entry:not(:has(.new)), :global(.folder-content .sidebar-entry) {
        border-bottom: 1px solid #aaa;
    }

    .sidebar-entry.over:not(:has(.new)) {
        border-bottom: 10px solid rgba(0, 79, 143, 0.7);
    }

    .sidebar-entry.over:has(.new) {
        border-top: 10px solid rgba(0, 79, 143, 0.7);
    }

    .active {
        background-color: #005193;
        border-left-width: 4px;
    }

    .buttons {
        display: flex;
        opacity: 0;
        position: absolute;
        right: 0.25rem;
        font-size: 1.3rem;
        justify-content: center;
        align-items: stretch;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 100vw;
        transition: opacity 0.25s ease;
    }

    .download, .delete, .edit, .clone, .sync {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease;
        text-decoration: none;
        color: white;
    }

    .sidebar-entry:hover .buttons {
        opacity: 1;
    }

    .download:hover {
        background-color: #1dad36;
    }

    .delete:hover {
        background-color: #b60000;
    }

    .edit:hover {
        background-color: #0083ef;
    }

    .clone:hover {
        background-color: #00568c;

    }

	.sync:hover {
		background-color: #8200F3;
	}

	.activeSync {
		color: limegreen;
	}
	
	.hasUpdate {
		color: #f59e0b;
		animation: pulse 1.5s ease-in-out infinite;
	}
	
	.hasUpdate:hover {
		background-color: #f59e0b;
		color: white;
	}
	
	/* Entry-level hasUpdate style */
	.sidebar-entry.hasUpdate {
		animation: none;
	}
	
	.sidebar-entry.hasUpdate:hover {
		background-color: #444;
	}
	
	.entry-content {
		display: flex;
		align-items: center;
		gap: 0.3rem;
		flex: 1;
		min-width: 0;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.entry-content.hasUpdate {
		color: #f59e0b;
		animation: none;
	}
	
	.update-icon {
		font-size: 1.1rem;
		color: #f59e0b;
		flex-shrink: 0;
		animation: bounce 1s ease-in-out infinite;
	}
	
	@keyframes bounce {
		0%, 100% {
			transform: translateY(0);
		}
		50% {
			transform: translateY(-2px);
		}
	}
	
	@keyframes pulse {
		0%, 100% {
			opacity: 1;
		}
		50% {
			opacity: 0.5;
		}
	}

    .modal-buttons {
        display: flex;
        justify-content: center;
    }

    .modal-buttons .button {
        margin-inline: 1rem;
    }

    .modal-delete {
        background-color: #b60000;
    }
</style>