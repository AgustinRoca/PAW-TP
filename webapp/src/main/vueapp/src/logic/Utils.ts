import i18n from "@/plugins/i18n";

export type ID = number | string;

export type Nullable<T> = null | T;

/**
 * We use this and not string directly to make it more readable
 */
export type Base64 = string;

export interface Hash<T> {
    [k: string]: T
}

export function isValidEmail(email: string): boolean {
    let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function _joinPath(start: string, end: string): string {
    if (start.endsWith('/') && end.startsWith('/')) {
        return start + end.substring(1);
    }
    if (!start.endsWith('/') && !end.startsWith('/')) {
        return start + '/' + end;
    }
    return start + end;
}

export function createPath(path: string): string {
    // @ts-ignore
    return _joinPath(process.env.VUE_APP_ROOT_PATH, path);
}

export function createApiPath(path: string): string {
    // @ts-ignore
    return _joinPath(createPath(process.env.VUE_APP_API_PATH), path);
}

export function getErrorMessage(code:number) {
    return i18n.t("Error"+code).toString();
}