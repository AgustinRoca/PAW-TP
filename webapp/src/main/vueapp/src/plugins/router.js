import Vue from 'vue';
import VueRouter from 'vue-router';
import store from "./vuex";

Vue.use(VueRouter);

function notAuthGuard(to,from,next) {
    return new Promise(async resolve => {
        if (!store.getters["auth/loggedIn"] && store.getters["auth/canRefreshUser"]) {
            console.log('notAuthGuard')
            await store.dispatch("auth/reload");
        }

        if (store.getters["auth/loggedIn"]) {
            if(!store.state.auth.user.verified){
                next({name:"Unverified"});
            }
            else if(store.state.auth.isDoctor){
                next({name:"MedicHome"});
            }
            else{
                next({name:"PatientHome"});
            }
        }else{
            next();
        }

        resolve();
    });
}

function patientGuard(to,from,next) {
    return new Promise(async resolve => {
        if (!store.getters["auth/loggedIn"] && store.getters["auth/canRefreshUser"]) {
            console.log('patientGuard')
            await store.dispatch("auth/reload");
        }

        if(store.getters["auth/loggedIn"]){
            if(!store.state.auth.user.verified){
                next({name:"Unverified"});
            }
            else if(store.state.auth.isDoctor){
                next({name:"MedicHome"});
            }
            else{
                next();
            }
        }else{
            next({name:"Login",params:{
                prevto:to
            }});
        }

        resolve();
    });
}

function doctorGuard(to,from,next) {
    return new Promise(async resolve => {
        if (!store.getters["auth/loggedIn"] && store.getters["auth/canRefreshUser"]) {
            console.log('doctorGuard')
            await store.dispatch("auth/reload");
        }

        if(store.getters["auth/loggedIn"]){
            if(!store.state.auth.user.verified){
                next({name:"Unverified"});
            }
            else if(store.state.auth.isDoctor){
                next();
            }
            else{
                next({name:"PatientHome"});
            }
        }else{
            next({name:"Login",params:{
                    prevto:to
                }});
        }

        resolve();
    });
}

function redirectHomeTopord(to) {
    if(store.state.auth.isDoctor){
        return "doctor/home";
    }else{
        return "patient/home";
    }
}

function unverifiedGuard(to,from,next) {
    return new Promise(async resolve => {
        if (!store.getters["auth/loggedIn"] && store.getters["auth/canRefreshUser"]) {
            await store.dispatch("auth/reload");
        }

        if(store.getters["auth/loggedIn"]){
            if(!store.state.auth.user.verified){
                next();
            } else {
                next({name:"Landing"});
            }
        }else{
            next({name:"Landing"});
        }

        resolve();
    });
}

function notAuthGuardOrPatient(to,from,next) {
    return new Promise(async resolve => {
        if (!store.getters["auth/loggedIn"] && store.getters["auth/canRefreshUser"]) {
            await store.dispatch("auth/reload");
        }

        if(store.getters["auth/loggedIn"]){
            if(!store.state.auth.user.verified){
                next({name:"Unverified"});
            }
            else if(store.state.auth.isDoctor){
                next({name:"MedicHome"});
            }
            else{
                next();
            }
        }else{
            next();
        }

        resolve();
    });
}

const routes = [
    {
        path: '/mediclist/:page([1-9][0-9]*)',
        name: 'MedicList',
        component: () => import('@/views/medicList.vue'),
        beforeEnter:notAuthGuardOrPatient
    }, 
    {
        path: '/mediclist',
        redirect: '/mediclist/1'
    },
    {
        path: '/home',
        redirect:redirectHomeTopord
    },
    {
        path: '/verify/:token?',
        name: 'Unverified',
        component: () => import('@/views/unverified.vue'),
        beforeEnter:unverifiedGuard
    },
    {
        path: '/',
        name: 'Landing',
        component: () => import('@/views/landing.vue'),
        beforeEnter:notAuthGuardOrPatient
    }, {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/authentication/login'),
        meta:{
            hideNav:true
        },
        beforeEnter:notAuthGuard
    }, {
        path: '/signup',
        name: 'Signup',
        component:()=>import("@/views/authentication/signup"),
        meta:{
            hideNav:true
        },
        beforeEnter:notAuthGuard
    },
    {
        path: '/signup/doctor',
        name: 'SignupDoctor',
        component:()=>import("@/views/authentication/signupDoctor"),
        meta:{
            hideNav:true
        },
        beforeEnter:notAuthGuard
    },
    {
        path: '/signup/patient',
        name: 'SignupPatient',
        component:()=>import("@/views/authentication/signupPatient"),
        meta:{
            hideNav:true
        },
        beforeEnter:notAuthGuard
    },
    {
        path: '/doctor/home',
        name: 'MedicHome',
        component:()=>import("@/views/medic/home"),
        beforeEnter:doctorGuard
    },
    {
        path: '/doctor/profile',
        name: 'MedicProfile',
        component:()=>import('@/views/medic/profile'),
        beforeEnter:doctorGuard
    },
    {
        path: '/patient/profile',
        name: 'PatientProfile',
        component:()=>import('@/views/patient/profile'),
        beforeEnter:patientGuard
    },
    {
        path: '/patient/home',
        name: 'PatientHome',
        component:()=>import('@/views/patient/home'),
        beforeEnter:patientGuard
    },
    {
        path: '/appointment/:memberId([0-9][0-9]*)/:weekNo(-?[0-9][0-9]*)',
        name: 'SelectAppointment',
        component:()=>import('@/views/selectAppointment'),
        beforeEnter:patientGuard
    },
    {
        path: '/patient/appointment/:doctorId([0-9][0-9]*)/:year([0-9][0-9]*)/:monthOfYear([0-9][0-9]*)/:dayOfMonth([0-9][0-9]*)/:hourOfDay([0-9][0-9]*)/:minuteOfHour([0-9][0-9]*)',
        name: 'RequestAppointment',
        component:()=>import('@/views/patient/requestAppointment'),
        beforeEnter:patientGuard
    },
    {
        path:"/404",
        name:"Error404",
        component:()=>import("@/views/error/404")
    },
    {
        path:"/403",
        name:"Error403",
        component:()=>import("@/views/error/403")
    },
    {
        path:"/500",
        name:"Error500",
        component:()=>import("@/views/error/500")
    },
    {
        path:"*",
        name:"Error404Fallback",
        component:()=>import("@/views/error/404")
    },
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
});

export default router;
