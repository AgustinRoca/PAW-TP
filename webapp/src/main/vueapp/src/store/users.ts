import container from '~/plugins/inversify.config';
import TYPES from '~/logic/types';
import {DefineActionTree, DefineMutationTree} from '~/store/utils/helper.types';
import {RootState} from '~/store/types/root.types';
import {Nullable} from '~/logic/Utils';
import {Module} from 'vuex';
import {UserService} from '~/logic/interfaces/services/UserService';
import {UserActionReturnTypes, UserActions, UserMutations, UserState} from '~/store/types/user.types';
import {User} from '~/logic/models/User';
import {authMutationTypes} from '~/store/types/auth.types';
import {APIError} from '~/logic/models/APIError';
import {UserDoctors, UserPatients} from '~/logic/interfaces/services/AuthService';

function getService(): UserService {
    return container.get(TYPES.Services.UserService);
}

const state = (): UserState => ({
    profilePictureTimestamp: null
});

const actions: DefineActionTree<UserActions, UserState, RootState, UserActionReturnTypes> = {
    async me({rootState, rootGetters, commit}): Promise<void> {
        if (rootGetters['auth/loggedIn'] || rootState.auth._userLoading.promise || rootState.auth._userLoading.loaded)
            return;

        let data: UserDoctors | UserPatients | APIError;
        try {
            data = await getService().me();
        } catch (e) {
            console.error(e);
            return;
        }

        commit('auth/setUser', authMutationTypes.setUser(data instanceof APIError ? null : data.user), {
            root: true
        });
        if (!(data instanceof APIError)) {
            if ((data as UserDoctors).doctors) {
                commit('auth/setDoctors', authMutationTypes.setDoctors((data as UserDoctors).doctors), {
                    root: true,
                });
            } else {
                commit('auth/setPatients', authMutationTypes.setPatients((data as UserPatients).patients), {
                    root: true,
                });
            }
        }
    },

    async getUser({state, commit}, {payload}): Promise<Nullable<User>> {
        try {
            return await getService().get(payload.id);
        } catch (e) {
            console.error(e);
            return null;
        }
    },

    async updateUser({state, commit, rootState}, {payload}) {
        if (!rootState.auth.user) return;

        try {
            let data = await getService().update(rootState.auth.user.id, payload);
            if (!(data instanceof APIError)) {
                commit('auth/setUser', authMutationTypes.setUser(data), {
                    root: true
                });
            }
            return data;
        } catch (e) {
            return null;
        }
    },

    async createAsDoctor({state, commit, dispatch}, {payload}) {
        try {
            let userDoctor = await getService().createAsDoctor(payload.doctor);
            if (userDoctor instanceof APIError) {
                return;
            }

            commit('auth/setUser', authMutationTypes.setUser(userDoctor.user), {
                root: true
            });
            commit('auth/setDoctors', authMutationTypes.setDoctors(userDoctor.doctors), {
                root: true
            });
        } catch (e) {
            console.error(e);
        }
    },

    async createAsPatient({state, commit}, {payload}) {
        try {
            let userPatient = await getService().createAsPatient(payload.patient);
            if (userPatient instanceof APIError) {
                return;
            }

            commit('auth/setUser', authMutationTypes.setUser(userPatient.user), {
                root: true
            });
            commit('auth/setPatients', authMutationTypes.setPatients(userPatient.patients), {
                root: true
            });
        } catch (e) {
            return null;
        }
    }
};

const mutations: DefineMutationTree<UserMutations, UserState> = {
    updateProfilePictureTimestamp(state) {
        state.profilePictureTimestamp = new Date().getTime();
    }
};

const store: Module<any, any> = {
    namespaced: true,
    actions,
    mutations,
    state
};

export default store;
