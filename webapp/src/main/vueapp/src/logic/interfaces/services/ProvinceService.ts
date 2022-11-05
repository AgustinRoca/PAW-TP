import {ID, Nullable} from '~/logic/Utils';
import {Province} from '~/logic/models/Province';
import {APIError} from '~/logic/models/APIError';

export interface ProvinceService {
    list(countryId: ID): Promise<Nullable<Province[]> | APIError>;

    get(countryId: ID, id: ID): Promise<Nullable<Province>>;
}
