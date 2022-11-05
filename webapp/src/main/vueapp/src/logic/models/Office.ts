import {GenericEntity} from '~/logic/models/utils/GenericEntity';
import {Locality} from '~/logic/models/Locality';

export class Office extends GenericEntity<Office> {
    private _id: number;
    private _name: string;
    private _phone: string;
    private _email: string;
    private _street: string;
    private _url: string;
    private _localityId: number;

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

    public get phone(): string {
        return this._phone;
    }

    public set phone(value: string) {
        this._phone = value;
    }

    public get email(): string {
        return this._email;
    }

    public set email(value: string) {
        this._email = value;
    }

    public get street(): string {
        return this._street;
    }

    public set street(value: string) {
        this._street = value;
    }

    public get url(): string {
        return this._url;
    }

    public set url(value: string) {
        this._url = value;
    }

    public get localityId(): number {
        return this._localityId;
    }

    public set localityId(value: number) {
        this._localityId = value;
    }
}
