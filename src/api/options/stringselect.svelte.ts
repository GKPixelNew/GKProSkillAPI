import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import StringSelectOption       from '$components/options/StringSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class StringSelect extends Requirements implements ComponentOption {
	component                   = StringSelectOption;
	name: string;
	key: string;
	data: string                = $state('');
	tooltip: string | undefined = $state();
	multiline: boolean          = false;

	constructor(name: string, key: string, def = '') {
		super();
		this.name = name;
		this.key  = key;
		this.data = def;
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	setMultiline = (multiline: boolean = true): this => {
		this.multiline = multiline;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new StringSelect(this.name, this.key, this.data);
		select.multiline = this.multiline;
		return select;
	};

	getData = (): { [key: string]: string } => {
		const data: { [key: string]: string } = {};
		// Convert actual newlines back to \n for YAML storage
		data[this.key] = this.multiline 
			? (this.data || '').replace(/\n/g, '\\n')
			: (this.data || '');
		return data;
	};

	getSummary = (): string => this.data;

	deserialize = (yaml: Unknown) => {
		const val = <string>yaml[this.key];
		if (val !== undefined) {
			// Convert \n strings to actual newlines for multiline display
			this.data = this.multiline ? val.replace(/\\n/g, '\n') : val;
		}
	};
}