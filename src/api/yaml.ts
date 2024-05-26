import YAML from 'yaml';
import { notify } from '$api/notification-service';

export const parseYaml = (data: string) => {
	try {
		return YAML.parse(data, {uniqueKeys:false});
	} catch (e: any) { // eslint-disable-line
		notify('Error parsing YAML: ' + (e.error || e.message) + '\n' + data);
		console.error(e);
		console.error(e.error);
		console.error(e.message);
		return {};
	}
};