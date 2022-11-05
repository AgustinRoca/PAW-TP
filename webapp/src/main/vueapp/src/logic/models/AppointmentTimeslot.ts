export class AppointmentTimeslot {
    private _hour: number;
    private _minute: number;
    private _duration: number;

    public get hour(): number {
        return this._hour;
    }

    public set hour(value: number) {
        this._hour = value;
    }

    public get minute(): number {
        return this._minute;
    }

    public set minute(value: number) {
        this._minute = value;
    }

    public get duration(): number {
        return this._duration;
    }

    public set duration(value: number) {
        this._duration = value;
    }
}
