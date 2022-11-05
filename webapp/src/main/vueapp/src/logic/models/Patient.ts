import {GenericEntity} from '~/logic/models/utils/GenericEntity';

export class Patient extends GenericEntity<Patient> {
    private _id: number;
    private _userId: number;
    private _firstName: string;
    private _surname: string;
    private _officeId: number;

    public get id(): number {
        return this._id;
    }

    public set id(value: number) {
        this._id = value;
    }

    public get userId(): number {
        return this._userId;
    }

    public set userId(value: number) {
        this._userId = value;
    }

    public get firstName(): string {
        return this._firstName;
    }

    public set firstName(value: string) {
        this._firstName = value;
    }

    public get surname(): string {
        return this._surname;
    }

    public set surname(value: string) {
        this._surname = value;
    }

    public get officeId(): number {
        return this._officeId;
    }

    public set officeId(value: number) {
        this._officeId = value;
    }
}
