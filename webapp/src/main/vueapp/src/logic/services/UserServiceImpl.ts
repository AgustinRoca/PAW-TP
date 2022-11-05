import {Nullable} from '~/logic/Utils';
import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {getPathWithId} from '~/logic/services/Utils';
import {APIError} from '~/logic/models/APIError';
import {CreateUserDoctor, CreateUserPatient, UpdateUser, UserService} from '~/logic/interfaces/services/UserService';
import {User} from '~/logic/models/User';
import {UserDoctors, UserPatients} from '~/logic/interfaces/services/AuthService';

export const UserMIME = {
    CREATE_PATIENT: 'application/vnd.user.patient.create.v1+json',
    CREATE_DOCTOR: 'application/vnd.user.doctor.create.v1+json',
    GET: 'application/vnd.user.get.v1+json',
    ME: 'application/vnd.user.me.v1+json',
    UPDATE: 'application/vnd.user.update.v1+json'
};

@injectable()
export class UserServiceImpl implements UserService {
    private static PATH = 'users';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async me(): Promise<UserDoctors | UserPatients | APIError> {
        let response = await this.rest.get<UserDoctors | UserPatients>(UserServiceImpl.PATH, {
            accepts: UserMIME.ME,
            retry: false
        });
        return response.response;
    }

    public async get(id: number): Promise<Nullable<User>> {
        let response = await this.rest.get<User>(getPathWithId(UserServiceImpl.PATH, id), {
            accepts: UserMIME.GET
        });
        return response.orElse(null);
    }

    public async createAsDoctor(doctor: CreateUserDoctor): Promise<UserDoctors | APIError> {
        let response = await this.rest.post<UserDoctors, CreateUserDoctor>(UserServiceImpl.PATH, {
            accepts: UserMIME.ME,
            data: doctor,
            contentType: UserMIME.CREATE_DOCTOR
        });
        return response.response;
    }

    public async createAsPatient(patient: CreateUserPatient): Promise<UserPatients | APIError> {
        let response = await this.rest.post<UserPatients, CreateUserPatient>(UserServiceImpl.PATH, {
            accepts: UserMIME.ME,
            data: patient,
            contentType: UserMIME.CREATE_PATIENT
        });
        return response.response;
    }

    public async update(id: number, user: UpdateUser): Promise<User | APIError> {
        let response = await this.rest.put<User, UpdateUser>(getPathWithId(UserServiceImpl.PATH, id), {
            accepts: UserMIME.GET,
            data: user,
            contentType: UserMIME.UPDATE
        });
        return response.response;
    }
}
