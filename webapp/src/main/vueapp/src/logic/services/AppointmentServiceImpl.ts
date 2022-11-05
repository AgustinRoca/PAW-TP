import {Nullable} from '~/logic/Utils';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';
import {AppointmentService, CreateAppointment} from '~/logic/interfaces/services/AppointmentService';
import {Appointment} from '~/logic/models/Appointment';
import {APIError} from '~/logic/models/APIError';
import {DateRange} from '~/logic/models/utils/DateRange';
import {DateTime} from 'luxon';

const AppointmentMIME = {
    LIST: 'application/vnd.appointment.list.get.v1+json',
    GET: 'application/vnd.appointment.get.v1+json',
    CREATE: 'application/vnd.appointment.create.v1+json'
};

@injectable()
export class AppointmentServiceImpl implements AppointmentService {
    private static PATH = 'appointments';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async get(id: number): Promise<Nullable<Appointment>> {
        let response = await this.rest.get<Appointment>(getPathWithId(AppointmentServiceImpl.PATH, id), {
            accepts: AppointmentMIME.GET
        });
        if (!response.isOk)
            return null;

        return AppointmentServiceImpl.formatAppointment(response.data!);
    }

    public async list(dateRange: DateRange): Promise<Appointment[] | APIError> {
        let response = await this.rest.get<Appointment[]>(
            AppointmentServiceImpl.PATH,
            {
                accepts: AppointmentMIME.LIST,
                params: {
                    from_year: dateRange.from.year,
                    from_month: dateRange.from.month,
                    from_day: dateRange.from.day,
                    to_year: dateRange.to.year,
                    to_month: dateRange.to.month,
                    to_day: dateRange.to.day,
                }
            }
        );
        if (!response.isOk)
            return response.error!;

        return AppointmentServiceImpl.formatAppointments(response.data!);
    }

    public async create(appointment: CreateAppointment): Promise<Appointment | APIError> {
        let response = await this.rest.post<Appointment, CreateAppointment>(AppointmentServiceImpl.PATH, {
            accepts: AppointmentMIME.GET,
            data: appointment,
            contentType: AppointmentMIME.CREATE
        });
        if (!response.isOk)
            return response.error!;

        return AppointmentServiceImpl.formatAppointment(response.data!);
    }

    public async delete(id: number): Promise<true | APIError> {
        let response = await this.rest.delete(getPathWithId(AppointmentServiceImpl.PATH, id));
        return response.isOk ? true : response.error!;
    }

    private static formatAppointments(appointments: Appointment[]): Appointment[] {
        let ret: Appointment[] = [];

        for (let appointment of appointments) {
            ret.push(AppointmentServiceImpl.formatAppointment(appointment));
        }

        return ret;
    }

    private static formatAppointment(appointment: Appointment): Appointment {
        //TODO: check
        //@ts-ignore
        let dateTime = DateTime.fromMillis(appointment.date_from).toUTC();
        //TODO: check if theres a better way
        appointment.dateFrom = new Date(
            dateTime.get("year"),
            dateTime.get("month")-1,
            dateTime.get("day"),
            dateTime.get("hour"),
            dateTime.get("minute")
        );
        appointment.dateTo = DateTime.fromJSDate(appointment.dateFrom).plus({ minutes: Appointment.DURATION_MINUTES }).toJSDate();
        return appointment;
    }
}
