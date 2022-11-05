import {ID, Nullable} from '~/logic/Utils';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {GetConfig, RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';
import {ProvinceService} from '~/logic/interfaces/services/ProvinceService';
import {Province} from '~/logic/models/Province';
import {APIError} from '~/logic/models/APIError';
import {CountryServiceImpl} from '~/logic/services/CountryServiceImpl';

const ProvinceMIME = {
    LIST: 'application/vnd.province.list.get.v1+json',
    GET: 'application/vnd.province.get.v1+json',
};

@injectable()
export class ProvinceServiceImpl implements ProvinceService {
    public static PATH = 'provinces';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async list(countryId: ID): Promise<Nullable<Province[]> | APIError> {
        let config: GetConfig<any> = {
            accepts: ProvinceMIME.LIST
        };

        let response = await this.rest.get<Province[]>(ProvinceServiceImpl.formatPath(countryId), config);
        return response.nullableResponse;
    }

    public async get(countryId: ID, id: ID): Promise<Nullable<Province>> {
        let response = await this.rest.get<Province>(ProvinceServiceImpl.formatPath(countryId, id), {
            accepts: ProvinceMIME.GET
        });
        return response.orElse(null);
    }

    private static formatPath(countryId: ID, provinceId?: ID): string {
        let s = `/${CountryServiceImpl.PATH}/${countryId}/${ProvinceServiceImpl.PATH}`;
        if (provinceId != null) return getPathWithId(s, provinceId);
        return s;
    }
}
