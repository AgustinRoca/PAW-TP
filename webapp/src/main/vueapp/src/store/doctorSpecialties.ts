import container from '~/plugins/inversify.config';
import TYPES from '~/logic/types';
import {DefineActionTree, DefineMutationTree} from '~/store/utils/helper.types';
import {RootState} from '~/store/types/root.types';
import {Module} from 'vuex';
import {
    DoctorSpecialtyActions, DoctorSpecialtyMutations,
    doctorSpecialtyMutationTypes,
    DoctorSpecialtyState,
} from '~/store/types/doctorSpecialties.types';
import {DoctorSpecialtyService} from '~/logic/interfaces/services/DoctorSpecialtyService';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';

function getService(): DoctorSpecialtyService {
    return container.get(TYPES.Services.DoctorSpecialtyService);
}

const state = (): DoctorSpecialtyState => ({
    _listLoading: {
        loaded: false,
        promise: null
    } as DoctorSpecialtyState['_listLoading'],
    doctorSpecialties: [] as DoctorSpecialty[]
});

const actions: DefineActionTree<DoctorSpecialtyActions, DoctorSpecialtyState, RootState> = {
    async loadDoctorSpecialties({state, commit}) {
        if (state._listLoading.loaded) return;
        if (state._listLoading.promise) return;

        let promise = getService().list();
        commit(doctorSpecialtyMutationTypes.setPromise(promise));

        let data: DoctorSpecialty[] = [];
        try {
            data = await promise;
        } catch (e) {
            return;
        }

        commit(doctorSpecialtyMutationTypes.setDoctorSpecialties(data));
    }
};

const mutations: DefineMutationTree<DoctorSpecialtyMutations, DoctorSpecialtyState> = {
    setPromise(state, {payload}): void {
        state._listLoading.promise = payload;
    },
    setDoctorSpecialties(state, {payload}): void {
        state.doctorSpecialties = payload;
        state._listLoading.loaded = true;
        state._listLoading.promise = null;
    }
};

const store: Module<any, any> = {
    namespaced: true,
    actions,
    mutations,
    state
};

export default store;
