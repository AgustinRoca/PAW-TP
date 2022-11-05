import {CacheableAsyncProperty, DefineTypes} from '~/store/utils/helper.types';
import {Locality} from '~/logic/models/Locality';
import {ID, Nullable} from '~/logic/Utils';
import {APIError} from '~/logic/models/APIError';

export interface LocalityState {
    localities: Locality[],
    _listLoading: CacheableAsyncProperty<Nullable<Locality[]> | APIError>
}

export interface LocalityActions {
    loadLocalities: {
        countryId: ID,
        provinceId: ID
    } | void
}

export const localityActionTypes: DefineTypes<LocalityActions> = {
    loadLocalities: payload => ({payload, type: 'loadLocalities'})
};

export interface LocalityMutations {
    setPromise: Promise<Nullable<Locality[]> | APIError>,
    setLocalities: Locality[]
}

export const localityMutationTypes: DefineTypes<LocalityMutations> = {
    setPromise: payload => ({payload, type: 'setPromise'}),
    setLocalities: payload => ({payload, type: 'setLocalities'})
};
