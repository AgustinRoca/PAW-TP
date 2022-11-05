import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';

export interface DoctorSpecialtyService {
    list(): Promise<DoctorSpecialty[]>;
}
