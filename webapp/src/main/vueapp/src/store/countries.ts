import container from '~/plugins/inversify.config';
import TYPES from '~/logic/types';
import {DefineActionTree, DefineMutationTree} from '~/store/utils/helper.types';
import {RootState} from '~/store/types/root.types';
import {Module} from 'vuex';
import {CountryService} from '~/logic/interfaces/services/CountryService';
import {CountryActions, CountryMutations, countryMutationTypes, CountryState} from '~/store/types/countries.types';
import {Country} from '~/logic/models/Country';

function getService(): CountryService {
    return container.get(TYPES.Services.CountryService);
}

const state = (): CountryState => ({
    _listLoading: {
        loaded: false,
        promise: null
    } as CountryState['_listLoading'],
    countries: [] as Country[]
});

const actions: DefineActionTree<CountryActions, CountryState, RootState> = {
    async loadCountries({state, commit}) {
        if (state._listLoading.loaded) return;
        if (state._listLoading.promise) return;

        let promise = getService().list();
        commit(countryMutationTypes.setPromise(promise));

        let data: Country[];
        try {
            data = await promise;
        } catch (e) {
            return;
        }

        commit(countryMutationTypes.setCountries(data));
    }
};

const mutations: DefineMutationTree<CountryMutations, CountryState> = {
    setPromise(state, {payload}): void {
        state._listLoading.promise = payload;
    },
    setCountries(state, {payload}): void {
        state.countries = payload;
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
