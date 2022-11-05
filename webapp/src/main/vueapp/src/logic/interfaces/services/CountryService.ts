import {Country} from '~/logic/models/Country';
import {Nullable} from '~/logic/Utils';

export interface CountryService {
    list(): Promise<Country[]>;

    get(id: string): Promise<Nullable<Country>>;
}
