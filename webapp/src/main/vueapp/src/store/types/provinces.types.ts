import {CacheableAsyncProperty, DefineTypes} from '~/store/utils/helper.types';
import {ID, Nullable} from '~/logic/Utils';
import {APIError} from '~/logic/models/APIError';
import {Province} from '~/logic/models/Province';

export interface ProvinceState {
    provinces: Province[],
    _listLoading: CacheableAsyncProperty<Nullable<Province[]> | APIError>
}

export interface ProvinceActions {
    loadProvinces: {
        countryId: ID
    }
}

export const provinceActionTypes: DefineTypes<ProvinceActions> = {
    loadProvinces: payload => ({payload, type: 'loadProvinces'})
};

export interface ProvinceMutations {
    setPromise: Promise<Nullable<Province[]> | APIError>,
    setProvinces: Province[]
}

export const provinceMutationTypes: DefineTypes<ProvinceMutations> = {
    setPromise: payload => ({payload, type: 'setPromise'}),
    setProvinces: payload => ({payload, type: 'setProvinces'})
};
