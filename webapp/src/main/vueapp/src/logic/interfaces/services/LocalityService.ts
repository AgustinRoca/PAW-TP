import {ID, Nullable} from '~/logic/Utils';
import {Locality} from '~/logic/models/Locality';
import {APIError} from '~/logic/models/APIError';

export interface LocalityService {
    list(params?: { countryId: ID, provinceId: ID }): Promise<Nullable<Locality[]> | APIError>;

    get(countryId: ID, provinceId: ID, id: ID): Promise<Nullable<Locality>>;

    getById(id: ID): Promise<Nullable<Locality>>;
}
