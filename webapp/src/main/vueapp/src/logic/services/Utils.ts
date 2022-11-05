export function getPathWithId(path: string, id: string | number): string {
    return path + '/' + encodeURIComponent(id.toString());
}

export const JSON_MIME = 'application/json';
