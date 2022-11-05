import {LocalityState} from '~/store/types/localities.types';
import {DoctorSpecialtyState} from '~/store/types/doctorSpecialties.types';
import {AuthState} from '~/store/types/auth.types';
import {UserState} from '~/store/types/user.types';
import {DoctorState} from '~/store/types/doctor.types';
import {ProvinceState} from '~/store/types/provinces.types';
import {CountryState} from '~/store/types/countries.types';

export interface RootState {
    auth: AuthState,
    localities: LocalityState,
    doctorSpecialties: DoctorSpecialtyState,
    users: UserState,
    doctors: DoctorState,
    countries: CountryState,
    provinces: ProvinceState
}
