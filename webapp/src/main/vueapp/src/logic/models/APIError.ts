export const ErrorMIME = 'application/vnd.error.v1+json';

export interface APISubError {
    code: number;
    message: string;
}

export class APIError {
    private readonly _code: number;
    private readonly _message: string;
    private readonly _errors: readonly APISubError[];

    constructor(code: number, message: string, errors: readonly APISubError[] = []) {
        this._code = code;
        this._message = message;
        this._errors = errors;
    }

    public get code(): number {
        return this._code;
    }

    public get message(): string {
        return this._message;
    }

    public get errors(): readonly APISubError[] {
        return this._errors;
    }
}
