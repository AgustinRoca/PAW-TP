export interface PaginationLinks {
    first: string;
    last: string;
    next?: string;
    previous?: string;
}

export class Pagination<T> {
    private readonly _items: T[];
    private readonly _links?: PaginationLinks;
    private readonly _totalItems: number;

    constructor(items: T[], totalItems: number, links?: PaginationLinks) {
        this._items = items;
        this._links = links;
        this._totalItems = totalItems;
    }

    public get items(): T[] {
        return this._items;
    }

    public get links(): PaginationLinks | undefined {
        return this._links;
    }

    public get totalItems(): number {
        return this._totalItems;
    }
}
