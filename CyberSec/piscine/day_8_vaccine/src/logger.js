import { styleText } from 'node:util';

export const printInfo = (...a) => console.log(styleText('yellow', "[INFO]"), ...a);
export const printError = (...a) => console.error(styleText('red', "[ERROR]"), ...a);
export const printSuccess = (...a) => console.log(styleText('green', "[SUCCESS]"), ...a);
export const printFatal = (...a) => console.error(styleText('magenta', "[FATAL]"), ...a);
