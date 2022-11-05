import {CacheableAsyncProperty, DefineTypes} from '~/store/utils/helper.types';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';

export interface DoctorSpecialtyState {
    doctorSpecialties: DoctorSpecialty[],
    _listLoading: CacheableAsyncProperty<DoctorSpecialty[]>
}

export interface DoctorSpecialtyActions {
    loadDoctorSpecialties: void
}

export const doctorSpecialtyActionTypes: DefineTypes<DoctorSpecialtyActions> = {
    loadDoctorSpecialties: payload => ({payload, type: 'loadDoctorSpecialties'})
};

export interface DoctorSpecialtyMutations {
    setPromise: Promise<DoctorSpecialty[]>,
    setDoctorSpecialties: DoctorSpecialty[]
}

export const doctorSpecialtyMutationTypes: DefineTypes<DoctorSpecialtyMutations> = {
    setPromise: payload => ({payload, type: 'setPromise'}),
    setDoctorSpecialties: payload => ({payload, type: 'setDoctorSpecialties'})
};
