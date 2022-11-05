import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {APIError} from '~/logic/models/APIError';
import {DateRange} from '~/logic/models/utils/DateRange';
import {AppointmentTimeSlotService} from '~/logic/interfaces/services/AppointmentTimeSlotService';
import {AppointmentTimeslotDate} from '~/logic/models/AppointmentTimeslotDate';
import {DoctorServiceImpl} from '~/logic/services/DoctorServiceImpl';

const AppointmentTimeSlotMIME = {
    LIST: 'application/vnd.appointment-timeslot.list.get.v1+json',
};

@injectable()
export class AppointmentTimeSlotServiceImpl implements AppointmentTimeSlotService {
    private static PATH = 'appointmentTimeSlots';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async list(doctorId: number, dateRange: DateRange): Promise<AppointmentTimeslotDate[] | APIError> {
        let path = `/${DoctorServiceImpl.PATH}/${doctorId}/${AppointmentTimeSlotServiceImpl.PATH}`;
        let response = await this.rest.get<AppointmentTimeslotDate[]>(
            path,
            {
                accepts: AppointmentTimeSlotMIME.LIST,
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
        return response.response;
    }
}
