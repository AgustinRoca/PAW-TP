import {GenericEntity} from '~/logic/models/utils/GenericEntity';
import {Nullable} from '~/logic/Utils';

export class User extends GenericEntity<User> {
    private _id: number;
    private _email: string;
    private _firstName: string;
    private _surname: string;
    private _verified: boolean;
    private _phone: Nullable<string>;
    private _hasPicture: boolean;

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

    public get verified(): boolean {
        return this._verified;
    }

    public set verified(value: boolean) {
        this._verified = value;
    }

    public get phone(): Nullable<string> {
        return this._phone;
    }

    public set phone(value: Nullable<string>) {
        this._phone = value;
    }

    public get hasPicture(): boolean {
        return this._hasPicture;
    }

    public set hasPicture(value: boolean) {
        this._hasPicture = value;
    }
}
