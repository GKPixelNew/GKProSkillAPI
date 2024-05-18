import { toast } from '@zerodevx/svelte-toast'

export const notifySuccess = (message: string) => toast.push(message, {
    theme: {
        '--toastBackground': '#21ba45',
        '--toastColor': 'white',
        '--toastBarBackground': 'olive'
    }
})

export const notifyFailure = (message: string) => toast.push(message, {
    theme: {
        '--toastBackground': '#c10015',
        '--toastColor': 'white',
        '--toastBarBackground': 'maroon'
    }
})