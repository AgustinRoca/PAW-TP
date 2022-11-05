import {Country} from '~/logic/models/Country';
import {Nullable} from '~/logic/Utils';
import {CountryService} from '~/logic/interfaces/services/CountryService';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';

const CountryServiceMIME = {
    LIST: 'application/vnd.country.list.get.v1+json',
    GET: 'application/vnd.country.get.v1+json',
};

@injectable()
export class CountryServiceImpl implements CountryService {
    public static PATH = 'countries';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async list(): Promise<Country[]> {
        let response = await this.rest.get<Country[]>(CountryServiceImpl.PATH, {
            accepts: CountryServiceMIME.LIST
        });
        return response.orElse([]);
    }

    public async get(id: string): Promise<Nullable<Country>> {
        let response = await this.rest.get<Country>(getPathWithId(CountryServiceImpl.PATH, id), {
            accepts: CountryServiceMIME.GET
        });
        return response.orElse(null);
    }
}
