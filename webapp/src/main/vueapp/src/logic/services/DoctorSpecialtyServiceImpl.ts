import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {DoctorSpecialtyService} from '~/logic/interfaces/services/DoctorSpecialtyService';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';

const DoctorSpecialtyMIME = {
    LIST: 'application/vnd.specialty.list.get.v1+json',
};

@injectable()
export class DoctorSpecialtyServiceImpl implements DoctorSpecialtyService {
    private static PATH = 'specialties';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async list(): Promise<DoctorSpecialty[]> {
        let response = await this.rest.get<DoctorSpecialty[]>(DoctorSpecialtyServiceImpl.PATH, {
            accepts: DoctorSpecialtyMIME.LIST
        });
        return response.orElse([]);
    }
}
