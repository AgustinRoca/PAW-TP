import container from '~/plugins/inversify.config';
import TYPES from '~/logic/types';
import {DefineActionTree} from '~/store/utils/helper.types';
import {RootState} from '~/store/types/root.types';
import {Module} from 'vuex';
import {APIError} from '~/logic/models/APIError';
import {DoctorService} from '~/logic/interfaces/services/DoctorService';
import {DoctorActionReturnTypes, DoctorActions, DoctorState} from '~/store/types/doctor.types';
import {Doctor} from '~/logic/models/Doctor';
import {authMutationTypes} from '~/store/types/auth.types';

const state = (): DoctorState => ({
});

function getService(): DoctorService {
    return container.get(TYPES.Services.DoctorService);
}

const actions: DefineActionTree<DoctorActions, DoctorState, RootState, DoctorActionReturnTypes> = {
    async updateDoctor({state, commit}, {payload}): Promise<Doctor | APIError> {
        let data = await getService().update(payload.id, payload.doctor);
        if (!(data instanceof APIError)) {
            commit('auth/setDoctor', authMutationTypes.setDoctor({
                id: payload.id,
                doctor: data
            }), {root: true});
        }

        return data;
    }
};

const store: Module<any, any> = {
    namespaced: true,
    actions,
    state
};

export default store;
