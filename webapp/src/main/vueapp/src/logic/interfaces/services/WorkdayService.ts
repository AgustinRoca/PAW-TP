import {APIError} from '~/logic/models/APIError';
import {Workday} from '~/logic/models/Workday';
import {Nullable} from '~/logic/Utils';
import {Day, WorkdayTime} from '~/logic/models/utils/DateRange';

export interface CreateWorkday {
    start: WorkdayTime,
    end: WorkdayTime,
    day: Day
}

export interface WorkdayService {
    list(): Promise<Workday[] | APIError>;

    get(id: number): Promise<Nullable<Workday>>;

    createList(createWorkdays: CreateWorkday[]): Promise<Workday[] | APIError>;

    delete(id: number): Promise<boolean>;
}
