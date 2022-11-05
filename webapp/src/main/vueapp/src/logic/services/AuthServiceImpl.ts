import {inject, injectable} from 'inversify';
import TYPES from '~/logic/types';
import {RestRepository} from '~/logic/interfaces/repositories/RestRepository';
import {AuthService, LoginUser, UserDoctors, UserPatients} from '~/logic/interfaces/services/AuthService';
import {User} from '~/logic/models/User';
import {APIError, ErrorMIME} from '~/logic/models/APIError';
import {JSON_MIME} from '~/logic/services/Utils';
import {UserMIME} from '~/logic/services/UserServiceImpl';

const AuthMIME = {
    LOGIN: 'application/vnd.login.post.v1+json'
};

@injectable()
export class AuthServiceImpl implements AuthService {
    private static LOGIN_PATH = 'login';
    private static REFRESH_PATH = 'auth/refresh';
    private static LOGOUT_PATH = 'auth/logout';

    @inject(TYPES.Repositories.RestRepository)
    private rest: RestRepository;

    public async login(loginUser: LoginUser): Promise<UserPatients | UserDoctors | APIError> {
        let response = await this.rest.post<UserPatients | UserDoctors, LoginUser>(AuthServiceImpl.LOGIN_PATH, {
            accepts: UserMIME.ME,
            data: loginUser,
            contentType: AuthMIME.LOGIN
        });

        return response.response;
    }

    public async reload(): Promise<UserPatients | UserDoctors | APIError> {
        let response = await this.rest.post<UserPatients | UserDoctors, {}>(
            AuthServiceImpl.REFRESH_PATH,
            {
                accepts: UserMIME.ME,
                data: {},
                contentType: JSON_MIME,
                retry: false
            },
            true
        );

        return response.response;
    }

    public async logout(): Promise<true | APIError> {
        let response = await this.rest.post<User, void>(AuthServiceImpl.LOGOUT_PATH, {
            accepts: ErrorMIME,
            data: undefined,
            contentType: JSON_MIME
        });

        return response.isOk ? true : response.error!;
    }
}
