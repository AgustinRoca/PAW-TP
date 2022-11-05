import {DefineTypes} from '~/store/utils/helper.types';
import {UpdateDoctor} from '~/logic/interfaces/services/DoctorService';
import {Doctor} from '~/logic/models/Doctor';
import {APIError} from '~/logic/models/APIError';

export interface DoctorState {}

export interface DoctorActions {
    updateDoctor: {
        id: number,
        doctor: UpdateDoctor
    }
}

export interface DoctorActionReturnTypes {
    updateDoctor: Promise<Doctor | APIError>,
}

export const doctorActionTypes: DefineTypes<DoctorActions> = {
    updateDoctor: payload => ({payload, type: 'updateDoctor'}),
};
