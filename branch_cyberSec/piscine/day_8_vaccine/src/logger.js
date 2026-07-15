import { styleText } from 'node:util';

export const print_info = (...a) => console.log(styleText('yellow', "[INFO]"), ...a);
export const print_error = (...a) => console.error(styleText('red', "[ERROR]"), ...a);
export const print_success = (...a) => console.log(styleText('green', "[SUCCESS]"), ...a);
export const print_fatal = (...a) => console.error(styleText(['magenta', 'bold'], "[FATAL]"), ...a);
