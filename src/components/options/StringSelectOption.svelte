<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';

	interface Props {
		data: string;
		name?: string | undefined;
		tooltip?: string | undefined;
		multiline?: boolean;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, multiline = false, onsave }: Props = $props();

	$effect(() => {
		if (data || !data) onsave?.();
	});
</script>

<ProInput label={name} {tooltip} bind:value={data} {multiline} />