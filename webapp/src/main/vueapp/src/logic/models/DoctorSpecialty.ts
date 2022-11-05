import {GenericEntity} from '~/logic/models/utils/GenericEntity';

export class DoctorSpecialty extends GenericEntity<DoctorSpecialty> {
    private _id: number;
    private _name: string;

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
}
