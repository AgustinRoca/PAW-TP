import container from '~/plugins/inversify.config';
import TYPES from '~/logic/types';
import {DefineActionTree, DefineMutationTree} from '~/store/utils/helper.types';
import {RootState} from '~/store/types/root.types';
import {Module} from 'vuex';
import {LocalityActions, LocalityMutations, localityMutationTypes, LocalityState} from '~/store/types/localities.types';
import {Locality} from '~/logic/models/Locality';
import {Nullable} from '~/logic/Utils';
import {APIError} from '~/logic/models/APIError';
import {ProvinceService} from '~/logic/interfaces/services/ProvinceService';
import {ProvinceActions, ProvinceMutations, provinceMutationTypes, ProvinceState} from '~/store/types/provinces.types';
import {Province} from '~/logic/models/Province';

function getService(): ProvinceService {
    return container.get(TYPES.Services.ProvinceService);
}

const state = (): ProvinceState => ({
    _listLoading: {
        loaded: false,
        promise: null
    } as ProvinceState['_listLoading'],
    provinces: [] as Province[]
});

const actions: DefineActionTree<ProvinceActions, ProvinceState, RootState> = {
    async loadProvinces({state, commit}, {payload}) {
        if (state._listLoading.loaded) return;
        if (state._listLoading.promise) return;

        let promise = getService().list(payload.countryId);
        commit(provinceMutationTypes.setPromise(promise));

        let data: Nullable<Province[]> | APIError = null;
        try {
            data = await promise;
        } catch (e) {
            return;
        }

        commit(provinceMutationTypes.setProvinces(data === null || data instanceof APIError ? [] : data));
    }
};

const mutations: DefineMutationTree<ProvinceMutations, ProvinceState> = {
    setPromise(state, {payload}): void {
        state._listLoading.promise = payload;
    },
    setProvinces(state, {payload}): void {
        state.provinces = payload;
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
