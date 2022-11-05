import {AuthService, UserDoctors, UserPatients} from '~/logic/interfaces/services/AuthService';
import {User} from '~/logic/models/User';
import TYPES from '~/logic/types';
import container from '~/plugins/inversify.config';
import {
    AuthActions,
    AuthGetters,
    AuthMutations,
    authMutationTypes,
    AuthState,
    DOCTORS_KEY,
    IS_DOCTOR_KEY,
    LOGGED_IN_EXPIRATION_DATE_KEY,
    PATIENTS_KEY,
    REFRESH_TOKEN_EXPIRATION_DATE_KEY,
    USER_KEY,
} from '~/store/types/auth.types';
import {RootState} from '~/store/types/root.types';
import {DefineActionTree, DefineGetterTree, DefineMutationTree} from '~/store/utils/helper.types';
import {Nullable} from '~/logic/Utils';
import {Module} from 'vuex';
import {APIError} from '~/logic/models/APIError';
import {Doctor} from '~/logic/models/Doctor';
import Vue from 'vue';

function getService(): AuthService {
    return container.get(TYPES.Services.AuthService);
}

const state = (): AuthState => ({
    _userLoading: {
        promise: null,
        loaded: false
    },
    loggingIn: false,
    loggingOut: false,
    user: null as Nullable<User>,
    doctors: [],
    patients: [],
    isDoctor: false,
    reloading: null
});

const getters: DefineGetterTree<AuthGetters, AuthState, RootState> = {
    loggedIn(state): boolean {
        return !!state.user;
    },
    canRefreshUser(): boolean {
        let refreshTokenExpDate = localStorage.getItem(REFRESH_TOKEN_EXPIRATION_DATE_KEY);
        return refreshTokenExpDate != null && (new Date()).getTime() < Number.parseInt(refreshTokenExpDate);
    }
};

const actions: DefineActionTree<AuthActions, AuthState, RootState> = {
    async invalidate({state, commit}) {
        if (state._userLoading.promise)
            await state._userLoading.promise;

        commit(authMutationTypes.setPromise(null));
        commit(authMutationTypes.setUser(null));
    },
    async login({state, commit}, {payload}) {
        if (state._userLoading.promise) return;

        let promise = getService().login({
            username: payload.email,
            password: payload.password
        });
        commit(authMutationTypes.setPromise(promise));

        let data: UserPatients | UserDoctors | APIError | null = null;
        try {
            data = await promise;
        } catch (e) {
        }

        commit(authMutationTypes.setUser(data === null || data instanceof APIError ? null : data.user));
        if (data != null && !(data instanceof APIError)) {
            if ((data as UserPatients).patients != null) {
                commit(authMutationTypes.setPatients((data as UserPatients).patients));
            } else {
                commit(authMutationTypes.setDoctors((data as UserDoctors).doctors));
            }
        }
    },
    async reload({state, commit}) {
        if (state._userLoading.promise) return state._userLoading.promise;

        let promise = getService().reload();

        commit(authMutationTypes.setReloading(promise));

        let data: UserPatients | UserDoctors | APIError | null = null;
        try {
            data = await promise;
        } catch (e) {
        }

        commit(authMutationTypes.setUser(data === null || data instanceof APIError ? null : data.user));
        if (data != null && !(data instanceof APIError)) {
            if ((data as UserPatients).patients != null) {
                commit(authMutationTypes.setPatients((data as UserPatients).patients));
            } else {
                commit(authMutationTypes.setDoctors((data as UserDoctors).doctors));
            }
        }

        return data;
    },
    async logout({state, commit}) {
        if (!state._userLoading.promise && !state.user) return;

        if (state._userLoading.promise)
            await state._userLoading.promise;

        commit(authMutationTypes.setPromise(null));
        try {
            let result = await getService().logout();
            if (!(result instanceof APIError)) {
                commit(authMutationTypes.setUser(null));
                commit(authMutationTypes.setPatients([]));
                commit(authMutationTypes.setDoctors([]));
            }
        } catch (e) {
        }
    }
};

const mutations: DefineMutationTree<AuthMutations, AuthState> = {
    setPromise(state, {payload}): void {
        if (payload) state.loggingIn = true;
        else state.loggingOut = true;
        state._userLoading.promise = payload;
    },
    setUser(state, {payload}): void {
        state.loggingOut = state.loggingIn = false;

        state.user = payload;
        if (payload) {
            localStorage.setItem(USER_KEY, JSON.stringify(payload));
        } else {
            localStorage.removeItem(USER_KEY);
            localStorage.removeItem(LOGGED_IN_EXPIRATION_DATE_KEY);
            localStorage.removeItem(REFRESH_TOKEN_EXPIRATION_DATE_KEY);
        }

        state._userLoading.promise = null;
        state._userLoading.loaded = true;
    },
    setPatients(state, {payload}): void {
        state.patients = payload || [];
        if (payload !== null)
            state.isDoctor = false;

        if (payload) {
            localStorage.setItem(PATIENTS_KEY, JSON.stringify(payload));
            localStorage.setItem(IS_DOCTOR_KEY, JSON.stringify(false));
        } else {
            localStorage.removeItem(PATIENTS_KEY);
        }
    },
    setDoctors(state, {payload}): void {
        state.doctors = payload || [];
        if (payload !== null)
            state.isDoctor = true;

        if (payload) {
            localStorage.setItem(DOCTORS_KEY, JSON.stringify(payload));
            localStorage.setItem(IS_DOCTOR_KEY, JSON.stringify(true));
        } else {
            localStorage.removeItem(DOCTORS_KEY);
        }
    },
    setDoctor(state, {payload}): void {
        let doctors: Doctor[];
        let dataString: Nullable<string> = localStorage.getItem(DOCTORS_KEY);
        if (!dataString || dataString.length === 0) doctors = [];
        doctors = JSON.parse(dataString!);

        if (doctors.length === 0) {
            if (payload.doctor) {
                state.doctors.push(payload.doctor);
                doctors.push(payload.doctor);
                localStorage.setItem(DOCTORS_KEY, JSON.stringify(doctors));
            }
            localStorage.setItem(DOCTORS_KEY, JSON.stringify(payload));
        } else {
            let index = doctors.findIndex(value => value.id.toString() == payload.id.toString());
            if (!payload.doctor) {
                if (index < 0) return;
                Vue.delete(state.doctors, index);
                doctors.splice(index, 1);
            } else {
                Vue.set(state.doctors, index, payload.doctor);
                doctors[index] = payload.doctor;
            }

            localStorage.setItem(DOCTORS_KEY, JSON.stringify(doctors));
        }
    },
    loadUserFromLocalStorage(state): void {
        let userString: Nullable<string> = localStorage.getItem(USER_KEY);
        if (!userString) return;

        let expirationDateString = localStorage.getItem(LOGGED_IN_EXPIRATION_DATE_KEY);
        if (!expirationDateString || expirationDateString.length === 0) {
            localStorage.removeItem(USER_KEY);
            localStorage.removeItem(DOCTORS_KEY);
            localStorage.removeItem(PATIENTS_KEY);
            localStorage.removeItem(IS_DOCTOR_KEY);
            return;
        }

        // Check epochs
        if ((new Date()).getTime() >= Number.parseInt(expirationDateString)) {
            localStorage.removeItem(LOGGED_IN_EXPIRATION_DATE_KEY);
            localStorage.removeItem(USER_KEY);
            localStorage.removeItem(DOCTORS_KEY);
            localStorage.removeItem(PATIENTS_KEY);
            localStorage.removeItem(IS_DOCTOR_KEY);
            return;
        }

        // Check epochs
        let refreshTokenExpDate = localStorage.getItem(REFRESH_TOKEN_EXPIRATION_DATE_KEY);
        if (!refreshTokenExpDate || (new Date()).getTime() >= Number.parseInt(refreshTokenExpDate)) {
            localStorage.removeItem(REFRESH_TOKEN_EXPIRATION_DATE_KEY);
            return;
        }

        let user, patients, doctors, isDoctor;
        try {
            user = JSON.parse(userString);
            patients = JSON.parse(localStorage.getItem(PATIENTS_KEY)!);
            doctors = JSON.parse(localStorage.getItem(DOCTORS_KEY)!);
            isDoctor = JSON.parse(localStorage.getItem(IS_DOCTOR_KEY)!);
        } catch (e) {
            return;
        }

        state.user = user;
        state.patients = patients || [];
        state.doctors = doctors || [];
        state.isDoctor = isDoctor;
    },
    setReloading(state, {payload}): void {
        state.reloading = payload;
    },
};

const store: Module<any, any> = {
    namespaced: true,
    actions,
    mutations,
    getters,
    state
};

export default store;
