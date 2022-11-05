import Vue from 'vue';
import Vuex from 'vuex';
import localities from '~/store/localities';
import doctorSpecialties from '~/store/doctorSpecialties';
import auth from '~/store/auth';
import doctors from '~/store/doctors';
import countries from '~/store/countries';
import provinces from '~/store/provinces';
import users from '~/store/users';

Vue.use(Vuex);

const store = new Vuex.Store({
    modules: {
        auth,
        localities,
        users,
        doctorSpecialties,
        doctors,
        countries,
        provinces
    }
});

export default store;
