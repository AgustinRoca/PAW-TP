import {GenericEntity} from '~/logic/models/utils/GenericEntity';

export class Locality extends GenericEntity<Locality> {
    private _id: number;
    private _name: string;
    private _provinceId: number;

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

    public get provinceId(): number {
        return this._provinceId;
    }

    public set provinceId(value: number) {
        this._provinceId = value;
    }
}
