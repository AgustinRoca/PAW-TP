import {GenericEntity} from '~/logic/models/utils/GenericEntity';
import {Nullable} from '~/logic/Utils';
import {Office} from '~/logic/models/Office';
import {User} from '~/logic/models/User';

export class Doctor extends GenericEntity<Doctor> {
    private _id: number;
    private _phone: Nullable<string>;
    private _email: string;
    private _registrationNumber: Nullable<number>;
    private _user: User;
    private _office: Office;
    private _specialtyIds: Array<number>;

    public get id(): number {
        return this._id;
    }

    public set id(value: number) {
        this._id = value;
    }

    public get email(): string {
        return this._email;
    }

    public set email(value: string) {
        this._email = value;
    }

    public get phone(): Nullable<string> {
        return this._phone;
    }

    public set phone(value: Nullable<string>) {
        this._phone = value;
    }

    public get registrationNumber(): Nullable<number> {
        return this._registrationNumber;
    }

    public set registrationNumber(value: Nullable<number>) {
        this._registrationNumber = value;
    }

    public get user(): User {
        return this._user;
    }

    public set user(value: User) {
        this._user = value;
    }

    public get office(): Office {
        return this._office;
    }

    public set office(value: Office) {
        this._office = value;
    }

    public get specialtyIds(): Array<number> {
        return this._specialtyIds;
    }

    public set specialtyIds(value: Array<number>) {
        this._specialtyIds = value;
    }
}
