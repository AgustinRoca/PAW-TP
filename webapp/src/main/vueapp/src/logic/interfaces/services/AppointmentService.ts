import {APIError} from '~/logic/models/APIError';
import {Nullable} from '~/logic/Utils';
import {Appointment} from '~/logic/models/Appointment';
import {DateRange} from '~/logic/models/utils/DateRange';

export interface CreateAppointment {
    date_from: {
        year: number;
        month: number;
        day: number;
        hour: number;
        minute: number;
    };
    message?: string;
    motive?: string;
    doctorId: number;
}

export interface AppointmentService {
    list(dateRange: DateRange): Promise<Appointment[] | APIError>;

    get(id: number): Promise<Nullable<Appointment>>;

    create(appointment: CreateAppointment): Promise<Appointment | APIError>;

    delete(id: number): Promise<true | APIError>
}
