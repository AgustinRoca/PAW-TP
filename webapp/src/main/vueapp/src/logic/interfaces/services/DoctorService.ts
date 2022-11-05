import {Doctor} from '~/logic/models/Doctor';
import {APIError} from '~/logic/models/APIError';
import {Pagination} from '~/logic/models/utils/Pagination';
import {ID, Nullable} from '~/logic/Utils';

export interface UpdateDoctor {
    phone?: Nullable<string>;
    email?: string;
    specialtyIds?: number[];
}

export interface DoctorSearchParams {
    page: number,
    perPage?: number,
    name?: string
    specialty?: ID,
    locality?: ID,
}

export interface DoctorService {
    list(params: DoctorSearchParams): Promise<Pagination<Doctor> | APIError>;

    get(id: number): Promise<Nullable<Doctor>>;

    update(id: number, doctor: UpdateDoctor): Promise<Doctor | APIError>
}
