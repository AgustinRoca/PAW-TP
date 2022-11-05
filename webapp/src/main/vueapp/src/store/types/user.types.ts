import {DefineTypes} from '~/store/utils/helper.types';
import {CreateUserDoctor, CreateUserPatient, UpdateUser} from '~/logic/interfaces/services/UserService';
import {Nullable} from '~/logic/Utils';
import {User} from '~/logic/models/User';

export interface UserState {
    profilePictureTimestamp: Nullable<number>;
}

export interface UserActions {
    me: void,
    getUser: {
        id: number
    }
    updateUser: UpdateUser,
    createAsDoctor: {
        doctor: CreateUserDoctor
    }
    createAsPatient: {
        patient: CreateUserPatient
    }
}

export interface UserActionReturnTypes {
    getUser: Promise<Nullable<User>>,
    updateUer: Promise<Nullable<User>>
}

export const userActionTypes: DefineTypes<UserActions> = {
    me: payload => ({payload, type: 'me'}),
    getUser: payload => ({payload, type: 'getUser'}),
    updateUser: (payload: UpdateUser) => ({payload, type: 'updateUser'}),
    createAsDoctor: payload => ({payload, type: 'createAsDoctor'}),
    createAsPatient: payload => ({payload, type: 'createAsPatient'})
};

export interface UserMutations {
    updateProfilePictureTimestamp: void;
}

export const userMutationTypes: DefineTypes<UserMutations> = {
    updateProfilePictureTimestamp: payload => ({payload, type: 'updateProfilePictureTimestamp'})
};
