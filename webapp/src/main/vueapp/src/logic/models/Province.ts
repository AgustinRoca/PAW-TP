import {GenericEntity} from '~/logic/models/utils/GenericEntity';

export class Province extends GenericEntity<Province> {
    private _id: number;
    private _name: string;
    private _countryId: number;

    public get id(): number {
        return this._id;
    }

    public set id(value: number) {
        this._id = value;
    }

    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }

    public get countryId(): number {
        return this._countryId;
    }

    public set country(value: number) {
        this._countryId = value;
    }
}
