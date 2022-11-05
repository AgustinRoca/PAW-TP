import {Nullable} from '~/logic/Utils';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';
import {APIError} from '~/logic/models/APIError';
import {CreateWorkday, WorkdayService} from '~/logic/interfaces/services/WorkdayService';
import {Workday} from '~/logic/models/Workday';

const WorkdayMIME = {
    LIST: 'application/vnd.workday.list.get.v1+json',
    GET: 'application/vnd.workday.get.v1+json',
    CREATE: 'application/vnd.workday.create.v1+json',
    CREATE_LIST: 'application/vnd.workday.list.create.v1+json'
};

@injectable()
export class WorkdayServiceImpl implements WorkdayService {
    private static PATH = 'workdays';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async get(id: number): Promise<Nullable<Workday>> {
        let response = await this.rest.get<Workday>(getPathWithId(WorkdayServiceImpl.PATH, id), {
            accepts: WorkdayMIME.GET
        });
        return response.orElse(null);
    }

    public async list(): Promise<Workday[] | APIError> {
        let response = await this.rest.get<Workday[]>(WorkdayServiceImpl.PATH, {
            accepts: WorkdayMIME.LIST
        });
        return response.response;
    }

    public async createList(createWorkdays: CreateWorkday[]): Promise<Workday[] | APIError> {
        let response = await this.rest.post<Workday[], CreateWorkday[]>(WorkdayServiceImpl.PATH, {
            accepts: WorkdayMIME.LIST,
            contentType: WorkdayMIME.CREATE_LIST,
            data: createWorkdays
        });

        return response.response;
    }

    public async delete(id: number): Promise<boolean> {
        let response = await this.rest.delete(getPathWithId(WorkdayServiceImpl.PATH, id));
        return response.isOk;
    }
}
