<script lang="ts">
    import { fade, fly } from 'svelte/transition';
    import { clickOutside } from '$api/clickoutside';

    interface Props {
        title?: string;
        message: string;
        confirmText?: string;
        cancelText?: string;
        confirmDanger?: boolean;
        onConfirm: () => void;
        onCancel: () => void;
    }

    let {
        title = '確認',
        message,
        confirmText = '確認',
        cancelText = '取消',
        confirmDanger = false,
        onConfirm,
        onCancel
    }: Props = $props();

    const handleKeydown = (e: KeyboardEvent) => {
        if (e.key === 'Escape') {
            e.preventDefault();
            onCancel();
        }
    };
</script>

<svelte:window onkeydown={handleKeydown} />

<div class="backdrop" transition:fade>
    <div class="dialog" 
         use:clickOutside={onCancel}
         transition:fly={{ y: -50 }}>
        <h3>{title}</h3>
        <p class="message">{message}</p>
        <div class="buttons">
            <button 
                class="btn cancel" 
                onclick={onCancel}
                tabindex="0">
                {cancelText}
            </button>
            <button 
                class="btn confirm" 
                class:danger={confirmDanger}
                onclick={onConfirm}
                tabindex="0">
                {confirmText}
            </button>
        </div>
    </div>
</div>

<style>
    .backdrop {
        position: fixed;
        inset: 0;
        z-index: 200;
        display: flex;
        justify-content: center;
        align-items: center;
        background: rgba(0, 0, 0, 0.7);
    }

    .dialog {
        background: var(--color-bg, #1a1a1a);
        border: 2px solid #444;
        border-radius: 8px;
        padding: 1.5rem;
        min-width: 300px;
        max-width: 450px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
    }

    h3 {
        margin: 0 0 1rem 0;
        color: #fff;
        font-size: 1.2rem;
    }

    .message {
        color: #ccc;
        margin: 0 0 1.5rem 0;
        line-height: 1.5;
        white-space: pre-wrap;
    }

    .buttons {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
    }

    .btn {
        padding: 0.5rem 1rem;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 0.9rem;
        transition: background-color 0.2s, transform 0.1s;
    }

    .btn:hover {
        transform: translateY(-1px);
    }

    .btn:active {
        transform: translateY(0);
    }

    .cancel {
        background: #444;
        color: #fff;
    }

    .cancel:hover {
        background: #555;
    }

    .confirm {
        background: #0083ef;
        color: #fff;
    }

    .confirm:hover {
        background: #0095ff;
    }

    .confirm.danger {
        background: #c10015;
    }

    .confirm.danger:hover {
        background: #e01020;
    }
</style>
