import {User} from '~/logic/models/User';
import {Patient} from '~/logic/models/Patient';
import {Doctor} from '~/logic/models/Doctor';
import {APIError} from '~/logic/models/APIError';

export interface LoginUser {
    username: string;
    password: string;
}

export interface UserPatients {
    user: User
    patients: Patient[]
}

export interface UserDoctors {
    user: User
    doctors: Doctor[]
}

export interface AuthService {
    login(loginUser: LoginUser): Promise<UserPatients | UserDoctors | APIError>;

    reload(): Promise<UserPatients | UserDoctors | APIError>;

    logout(): Promise<true | APIError>;
}
