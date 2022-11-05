import {GenericEntity} from '~/logic/models/utils/GenericEntity';
import {Patient} from '~/logic/models/Patient';

export enum AppointmentStatus {
    PENDING= 'PENDING',
    COMPLETE = 'COMPLETE',
    CANCELLED = 'CANCELLED',
    WAITING = 'WAITING',
    SEEN = 'SEEN'
}

export class Appointment extends GenericEntity<Appointment> {
    public static readonly DURATION_MINUTES = 15;

    private _id: number;
    private _status: AppointmentStatus;
    private _dateFrom: Date;
    private _dateTo: Date;
    private _message: string;
    private _motive: string;
    private _patient: Patient;
    private _doctorId: number;

    public get id(): number {
        return this._id;
    }

    public set id(value: number) {
        this._id = value;
    }

    public get status(): AppointmentStatus {
        return this._status;
    }

    public set status(value: AppointmentStatus) {
        this._status = value;
    }

    public get dateFrom(): Date {
        return this._dateFrom;
    }

    public set dateFrom(value: Date) {
        this._dateFrom = value;
    }

    public get dateTo(): Date {
        return this._dateTo;
    }

    public set dateTo(value: Date) {
        this._dateTo = value;
    }

    public get message(): string {
        return this._message;
    }

    public set message(value: string) {
        this._message = value;
    }

    public get motive(): string {
        return this._motive;
    }

    public set motive(value: string) {
        this._motive = value;
    }

    public get patient(): Patient {
        return this._patient;
    }

    public set patient(value: Patient) {
        this._patient = value;
    }

    public get doctorId(): number {
        return this._doctorId;
    }

    public set doctorId(value: number) {
        this._doctorId = value;
    }
}
