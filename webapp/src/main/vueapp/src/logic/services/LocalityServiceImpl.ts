import {ID, Nullable} from '~/logic/Utils';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';
import {LocalityService} from '~/logic/interfaces/services/LocalityService';
import {Locality} from '~/logic/models/Locality';
import {APIError} from '~/logic/models/APIError';
import {ProvinceServiceImpl} from '~/logic/services/ProvinceServiceImpl';
import {CountryServiceImpl} from '~/logic/services/CountryServiceImpl';

const LocalityMIME = {
    LIST: 'application/vnd.locality.list.get.v1+json',
    GET: 'application/vnd.locality.get.v1+json',
};

@injectable()
export class LocalityServiceImpl implements LocalityService {
    private static PATH = 'localities';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async list(params?: { countryId: ID, provinceId: ID }): Promise<Nullable<Locality[]> | APIError> {
        let path: string;
        if (params)
            path = LocalityServiceImpl.formatPath(params.countryId, params.provinceId);
        else
            path = LocalityServiceImpl.PATH;

        let response = await this.rest.get<Locality[]>(path, {
            accepts: LocalityMIME.LIST,
        });
        return response.nullableResponse;
    }

    public async get(countryId: ID, provinceId: ID, id: ID): Promise<Nullable<Locality>> {
        let response = await this.rest.get<Locality>(LocalityServiceImpl.formatPath(countryId, provinceId, id), {
            accepts: LocalityMIME.GET
        });
        return response.orElse(null);
    }

    public async getById(id: ID): Promise<Nullable<Locality>> {
        let response = await this.rest.get<Locality>(LocalityServiceImpl.formatPathId(id), {
            accepts: LocalityMIME.GET
        });
        return response.orElse(null);
    }

    private static formatPath(countryId: ID, provinceId: ID, localityId?: ID): string {
        let s = `/${CountryServiceImpl.PATH}/${countryId}/${ProvinceServiceImpl.PATH}/${provinceId}/${LocalityServiceImpl.PATH}`;
        if (localityId != null) return getPathWithId(s, localityId);
        return s;
    }

    private static formatPathId(localityId: ID): string {
        return getPathWithId(`/${LocalityServiceImpl.PATH}`, localityId);
    }
}
