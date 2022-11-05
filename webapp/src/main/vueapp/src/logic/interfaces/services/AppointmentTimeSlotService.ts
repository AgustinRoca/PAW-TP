import {DateRange} from '~/logic/models/utils/DateRange';
import {AppointmentTimeslotDate} from '~/logic/models/AppointmentTimeslotDate';
import {APIError} from '~/logic/models/APIError';

export interface AppointmentTimeSlotService {
    list(doctorId: number, dateRange: DateRange): Promise<AppointmentTimeslotDate[] | APIError>;
}
