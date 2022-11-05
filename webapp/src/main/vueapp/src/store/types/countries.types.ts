import {CacheableAsyncProperty, DefineTypes} from '~/store/utils/helper.types';
import {APIError} from '~/logic/models/APIError';
import {Country} from '~/logic/models/Country';

export interface CountryState {
    countries: Country[],
    _listLoading: CacheableAsyncProperty<Country[] | APIError>
}

export interface CountryActions {
    loadCountries: void
}

export const countryActionTypes: DefineTypes<CountryActions> = {
    loadCountries: payload => ({payload, type: 'loadCountries'})
};

export interface CountryMutations {
    setPromise: Promise<Country[] | APIError>,
    setCountries: Country[]
}

export const countryMutationTypes: DefineTypes<CountryMutations> = {
    setPromise: payload => ({payload, type: 'setPromise'}),
    setCountries: payload => ({payload, type: 'setCountries'})
};
