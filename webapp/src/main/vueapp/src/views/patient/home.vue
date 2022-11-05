<template>
    <div class="container h-75 w-100 mt-5">
        <div class="row">
            <h4>{{ $t('MyAppointments') }}</h4>
        </div>
        <div class="row h-100">
            <div class="col h-100 pl-0 mr-5 w-100">
                <ul class="list-group turno-list mr-2 w-100 h-100 overflow-auto">
                    <div v-if="!appointments.length" class="container-fluid justify-content-center">
                        <p class="text-left mt-4" style="color:grey;">
                            {{$t("NoAppointments")}}
                        </p>
                    </div>
                    <li v-for="appointment in appointments" :key="appointment.id"
                        class="list-group-item turno-item mb-3">
                        <div class="container">
                            <div class="row">
                                <div class="col-4 d-flex flex-column justify-content-center">
                                    <div class="profile-picture-container">
                                        <div style="margin-top: 100%;"></div>
                                        <img
                                            class="profile-picture rounded-circle"
                                            :src="getApiUrl('/users/' + appointmentDoctors[appointment.id].user.id + '/picture')"
                                            alt="profile pic"
                                        />
                                    </div>
                                </div>
                                <div class="col-7">
                                    <div class="row justify-content-start">
                                        <h5>{{ appointmentDoctors[appointment.id] ? appointmentDoctors[appointment.id].user.firstName : '' }}
                                            {{ appointmentDoctors[appointment.id] ? appointmentDoctors[appointment.id].user.surname : '' }}</h5>
                                    </div>
                                    <div class="row">
                                        <p class="m-0">
                                            {{ appointmentDoctors[appointment.id] ? appointmentDoctors[appointment.id].specialtyIds.map((v) => {
                                                    return getSpecialtyName(v);
                                                }).join(', ') : ''
                                            }}
                                        </p>
                                    </div>
                                    <div class="row">
                                        <p class="m-0">{{ appointmentDoctors[appointment.id] ? appointmentDoctors[appointment.id].office.street : '' }}</p>
                                    </div>
                                    <div class="row">
                                        <p class="m-0">
                                            {{
                                                $t(
                                                    'dow_dom_moy_sh_sm_eh_em',
                                                    [
                                                        getDoW(appointment.dateFrom.getDay()),
                                                        appointment.dateFrom.getDate(),
                                                        getMoY(appointment.dateFrom.getMonth()),
                                                        timeWithZero(appointment.dateFrom.getHours()),
                                                        timeWithZero(appointment.dateFrom.getMinutes()),
                                                        timeWithZero(appointment.dateTo.getHours()),
                                                        timeWithZero(appointment.dateTo.getMinutes())
                                                    ]
                                                )
                                            }}
                                        </p>
                                    </div>
                                </div>
                                <div class="col-1 justify-content-start align-items-center">
                                    <button class="btn" v-if="beingCanceled[appointment.id]" >
                                        <b-spinner small></b-spinner>
                                    </button>
                                    <button v-else class="btn" type="button" @click="cancelAppointment(appointment.id)">X</button>
                                </div>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="col">
                <form class="container p-5 filter-form" @submit="submitForm">
                    <div class="row justify-content-start">
                        <h3 class="form-title">{{ $t('SearchMedics') }}</h3>
                    </div>
                    <div class="row justify-content-start my-3">
                        <input v-model="name" class="w-100 form-control" type="text" name="name" id="name"
                               :placeholder='$t("NameAndOrSurname")'>
                    </div>
                    <div class="row justify-content-start my-3">
                        <select v-model="specialtyId" name="specialties" class="form-control">
                            <option :value="null" disabled selected>{{ $t('Specialty') }}</option>
                            <option :value="null">{{ $t('Any') }}</option>
                             <option v-for="specialty in specialties" :key="specialty.id"
                                    :value="specialty.id">{{ specialty.name }}</option>
                        </select>
                    </div>
                    <div class="row justify-content-start my-3">
                        <select v-model="localityId" name="localities" class="form-control">
                            <option :value="null" disabled selected>{{ $t('Locality') }}</option>
                            <option :value="null">{{ $t('Any') }}</option>
                            <option v-for="locality in localities" :key="locality.id"
                                    :value="locality.id">{{ locality.name }}</option>
                        </select>
                    </div>
                    <div class="row justify-content-start my-3">
                        <button type="submit" class="w-100 btn rounded-pill btn-light header-btn-element">{{ $t('SearchMedics') }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import moreOptions from '@/assets/moreOptions.svg';
import {Component, Vue, Watch} from 'vue-property-decorator';
import {State} from 'vuex-class';
import {AppointmentService} from '~/logic/interfaces/services/AppointmentService';
import {APIError} from '~/logic/models/APIError';
import {Appointment} from '~/logic/models/Appointment';
import TYPES from '~/logic/types';
import {createApiPath, createPath, Hash, ID, Nullable} from '~/logic/Utils';
import {Doctor} from '~/logic/models/Doctor';
import {DoctorService} from '~/logic/interfaces/services/DoctorService';
import {doctorSpecialtyActionTypes} from '~/store/types/doctorSpecialties.types';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';
import {Locality} from '~/logic/models/Locality';
import {DateTime} from 'luxon';
import {User} from '~/logic/models/User';

@Component
export default class PatientHome extends Vue {
    @State(state => state.auth.user)
    private readonly user: Nullable<User>;

    private moreOptions = moreOptions;
    private appointments:Appointment[] = [];
    @State(state => state.localities.localities)
    private readonly localities: Locality[];
    @State(state => state.doctorSpecialties.doctorSpecialties)
    private readonly specialties: DoctorSpecialty[];

    private name: string = '';
    private localityId: Nullable<number> = null;
    private specialtyId: Nullable<number> = null;
    private appointmentDoctors: Record<ID, Doctor> = {};

    private beingCanceled = {};

    @Watch('user', {immediate: true})
    guardPage(): void {
        if (!this.user) {
            this.$router.push({
                name: 'Landing',
            }).catch(() => {});
        }
    }

    async mounted(){
        this.$store.dispatch('doctorSpecialties/loadDoctorSpecialties', doctorSpecialtyActionTypes.loadDoctorSpecialties());
        let today = new Date();
        let twoWeeks = DateTime.fromJSDate(today).plus({ days: 13 }).toJSDate();
        let appointments = await this.getService().list(
            {
                from:{
                    year: today.getFullYear(),
                    month: today.getMonth() + 1,
                    day: today.getDate()
                },
                to:{
                    year: twoWeeks.getFullYear(),
                    month: twoWeeks.getMonth() + 1,
                    day: twoWeeks.getDate()
                }
            }
        );
        if (!(appointments instanceof APIError)) {
            this.appointmentDoctors = {};
            for (let appointment of appointments) {
                // Hacemos async para no perder tiempo, no hace falta que sea bloqueante
                await this.setAppointmentDoctor(appointment.id, appointment.doctorId);
            }
            this.appointments = appointments;
        }
    }

    async setAppointmentDoctor(appointmentId: ID, id: number) {
        let doctor = await this.$container.get<DoctorService>(TYPES.Services.DoctorService).get(id);
        this.appointmentDoctors[appointmentId] = doctor!;
    }

    getDoW(t: number): string {
        switch (t) {
            case 1:
                return this.$t('Monday').toString();
            case 2:
                return this.$t('Tuesday').toString();
            case 3:
                return this.$t('Wednesday').toString();
            case 4:
                return this.$t('Thursday').toString();
            case 5:
                return this.$t('Friday').toString();
            case 6:
                return this.$t('Saturday').toString();
            case 0:
                return this.$t('Sunday').toString();
            default:
                return t.toString();
        }
    }

    getMoY(t: number): string {
        switch (t+1) {
            case 1:
                return this.$t('January').toString();
            case 2:
                return this.$t('February').toString();
            case 3:
                return this.$t('March').toString();
            case 4:
                return this.$t('April').toString();
            case 5:
                return this.$t('May').toString();
            case 6:
                return this.$t('June').toString();
            case 7:
                return this.$t('July').toString();
            case 8:
                return this.$t('August').toString();
            case 9:
                return this.$t('September').toString();
            case 10:
                return this.$t('October').toString();
            case 11:
                return this.$t('November').toString();
            case 12:
                return this.$t('December').toString();
            default:
                return t.toString();
        }
    }

    timeWithZero(t: number): string {
        if (t < 10) {
            return '0' + t;
        } else {
            return t.toString();
        }
    }

    getSpecialtyName(id: number): string {
        for (let specialty of this.specialties) {
            if (specialty.id === id) return specialty.name;
        }

        return id.toString();
    }

    getApiUrl(url:string):string{
        return createApiPath(url);
    }

    submitForm(event: Event): void {
        event.preventDefault();
        event.stopPropagation();

        this.search();
    }

    async cancelAppointment(id: number): Promise<void> {
        let shouldDelete = await this.$bvModal.msgBoxConfirm(
            this.$t("DoYouWantToContinue").toString(),
            {
                cancelTitle:this.$t("No").toString(),
                okTitle: this.$t("Yes").toString(),
                okVariant:"danger",
                title:this.$t("YouAreAboutToCancelAnAppointment").toString()
            }
        )
        if(shouldDelete){
            Vue.set(this.beingCanceled,id,true);
            let response = await this.getService().delete(id);
            if (!(response instanceof APIError)) {
                let index = this.appointments.findIndex(value => value.id == id);
                if (index < 0) return;
                this.appointments.splice(index, 1);
            }
            Vue.delete(this.beingCanceled,id);
        }
    }

    search(): void {
        let query: Hash<string | string[]> = {};
        if (this.name)
            query.name = this.name.trim();
        if (this.localityId)
            query.localities = [this.localityId.toString()];
        if (this.specialtyId)
            query.specialties = [this.specialtyId.toString()];

        this.$router.push({
            path: createPath("/mediclist"),
            query
        }).catch(()=>{});
    }

    private getService(): AppointmentService {
        return this.$container.get<AppointmentService>(TYPES.Services.AppointmentService);
    }
}
</script>

<style scoped>

.header {
    background-color: #00C4BA;
}

.header-brand {
    font-weight: bold;
}

.header-brand:hover {
    font-weight: bold;
    color: white !important;
}

.header-a-element {
    color: white;
}

.header-a-element:hover {
    color: #e0e0e0;
}

.header-btn-element {
    color: #00C4BA;
    font-weight: bold;
}

.header-btn-element:hover {
    color: rgb(0, 160, 152);
    font-weight: bold;
}

.green-text {
    color: #00C4BA;
}

#navbar-logo {
    width: 2em;
}

.filter-form {
    background-color: #00C4BA;
    border-radius: 1em;
}

.form-title {
    color: white;
}

.form-control {
    background-color: rgb(214, 214, 214);
}

.white-text {
    color: white !important;
}

#navbarUserImage {
    width: 3em;
}

.turno-item {
    border-radius: 2em !important;
    background-color: rgb(214, 214, 214);
}

.turno-list {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

.turno-list::-webkit-scrollbar {
    display: none;
}

.moreOptionsButton {
    height: 1.5em;
    cursor: pointer;
}

.profile-picture-container {
    display: inline-block;
    position: relative;
    width: 100%;
}

.profile-picture {
    object-fit: cover;
    height: 100%;
    width: 100%;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
    position: absolute;
}

</style>