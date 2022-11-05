<template>
    <div class="container h-75 w-100 mt-5">
        <div class="row h-100">
            <div class="col-4 h-100 pl-0 mr-3 w-100">
                <h4>{{ $t('AgendaFor') }} {{ $t('today') }}</h4>
                <ul class="list-group turno-list mr-2 w-100 h-100 overflow-auto">
                    <div v-if="todayAppointments.length == 0" class="container-fluid justify-content-center">
                        <p class="text-left mt-4" style="color:grey;">{{ $t('NoAppointmentsToday') }}</p>
                    </div>
                    <li v-for="appointment in todayAppointments" :key="appointment.id"
                        class="list-group-item turno-item mb-3" id="lit"
                    >
                        <div class="container">
                            <div class="row">
                                <div class="col-4 d-flex flex-column justify-content-center">
                                    <div class="profile-picture-container">
                                        <div style="margin-top: 100%;"></div>
                                        <img
                                            class="profile-picture rounded-circle"
                                            :src='getApiUrl("/users/" + appointment.patient.userId + "/picture")'
                                            alt="profile pic"
                                        />
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="row justify-content-start">
                                        <h5>
                                            {{
                                                $t('name_surname', [appointment.patient.firstName, appointment.patient.surname])
                                            }}</h5>
                                    </div>
                                    <div class="row">
                                        <p class="m-0">
                                            {{
                                                $t(
                                                    'fhom_fmoh_thod_tmoh',
                                                    [
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
                                <div class="col-2 justify-content-start">
                                    <button class="btn" v-if="beingCanceled[appointment.id]" >
                                        <b-spinner small></b-spinner>
                                    </button>
                                    <button v-else type="button" class="btn" @click="cancelAppointment(appointment.id, today.getDay())">X</button>
                                </div>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="col">
                <h4>{{ $t('WeeklyAgenda') }}</h4>
                <table class="table table-borderless">
                    <tr>
                        <td class="px-0">
                            <button type="button" class="btn" id="prevWeekBtn" @click="setDisplayWeek(-1)">{{ prev }}</button>
                        </td>
                        <td v-for="i in 7" :key="i" class="px-0">
                            <!-- day of the week -->
                            <span
                                class="medicare-day-span container px-0 mx-2 d-flex flex-column align-items-center text-center"
                                @click="selectDay(i)"
                                :data-day="monday.plusDays(i)"
                                :style='(monday.plusDays(i-1).getTime() == selectedDay.getTime())?"font-weight:bold":""'>
                                <p class="mb-0"
                                   v-if="monday.plusDays(i-1).getDay() == 1">{{ $t('MondayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 2">{{ $t('TuesdayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 3">{{ $t('WednesdayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 4">{{ $t('ThursdayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 5">{{ $t('FridayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 6">{{ $t('SaturdayAbbreviated') }}</p>
                                <p class="mb-0"
                                   v-else-if="monday.plusDays(i-1).getDay() == 0">{{ $t('SundayAbbreviated') }}</p>
                                <p class="mb-0" v-else>{{ monday.plusDays(i - 1).getDay() }}</p>
                                <!-- day/month -->
                                <p class="my-0">
                                    {{
                                        $t('dom_moy', [monday.plusDays(i - 1).getDate(), getMpdMonthOfYear(monday.plusDays(i - 1).getMonth())])
                                    }}
                                </p>
                                <p>
                                    {{
                                        $t(
                                            'NumberedAppointments',
                                            [
                                                weekAppointments[monday.plusDays(i - 1).getDay()].length,
                                                (weekAppointments[monday.plusDays(i - 1).getDay()].length == 1) ?
                                                    $t('appointmentAbbreviated') :
                                                    $t('appointmentsAbbreviated')
                                            ]
                                        )
                                    }}
                                </p>
                            </span>
                        </td>
                        <td class="px-0">
                            <button type="button" class="btn" id="nextWeekBtn" @click="setDisplayWeek(1)">{{ next }}</button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="9">
                            <div class="container-fluid d-flex justify-content-center">
                                <ul class="list-group turno-list mr-2 w-50 overflow-auto">
                                    <div v-if="weekAppointments[selectedDay.getDay()].length == 0"
                                         class="container-fluid justify-content-center">
                                        <p class="text-center mt-4" style="color:grey;">
                                            {{ $t('NoAppointmentsThisDay') }}</p>
                                    </div>
                                    <li v-for="appointment in weekAppointments[selectedDay.getDay()]" :key="appointment.id"
                                        class="list-group-item turno-item mb-3">
                                        <div class="container">
                                            <div class="row">
                                                <div class="col-4 d-flex flex-column justify-content-center">
                                                    <div class="profile-picture-container">
                                                        <div style="margin-top: 100%;"></div>
                                                        <img
                                                            class="profile-picture rounded-circle"
                                                            :src="getApiUrl('/users/' + appointment.patient.userId + '/picture')"
                                                            alt="profile pic"
                                                        />
                                                    </div>
                                                </div>
                                                <div class="col-6">
                                                    <div class="row justify-content-start">
                                                        <h5>
                                                            {{
                                                                appointment.patient.firstName + ' ' + appointment.patient.surname
                                                            }}</h5>
                                                    </div>
                                                    <div class="row">
                                                        <p class="m-0">
                                                            {{
                                                                $t(
                                                                    'fhom_fmoh_thod_tmoh',
                                                                    [
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
                                                <div class="col-2 justify-content-start">
                                                    <button class="btn" v-if="beingCanceled[appointment.id]" >
                                                        <b-spinner small></b-spinner>
                                                    </button>
                                                    <button v-else type="button" class="btn"
                                                            @click="cancelAppointment(appointment.id, selectedDay.getDay())"
                                                    >
                                                        X
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import moreOptions from '@/assets/moreOptions.svg';
import {Component, Vue, Watch} from 'vue-property-decorator';
import {AppointmentService} from '~/logic/interfaces/services/AppointmentService';
import {APIError} from '~/logic/models/APIError';
import {Appointment} from '~/logic/models/Appointment';
import TYPES from '~/logic/types';
import {createApiPath, Nullable} from '~/logic/Utils';
import {DateTime} from 'luxon';
import {State} from 'vuex-class';
import {User} from '~/logic/models/User';

// @ts-ignore
Date.prototype.plusDays = function (i) {
    let date = new Date(this.valueOf());
    date.setDate(date.getDate() + i);
    return date;
};

@Component
export default class MedicHome extends Vue {
    @State(state => state.auth.user)
    private readonly user: Nullable<User>;

    private next = '>';
    private prev = '<';
    private moreOptions = moreOptions;
    private monday = this.getMonday(new Date());
    private selectedDay = new Date();
    private today = new Date();
    private todayAppointments: Appointment[] = [];
    private weekAppointments: Appointment[][] = [[], [], [], [], [], [], []];

    private beingCanceled = {};


    mounted(): Promise<void[]> {
        let promises = [];
        promises.push(this.updateTodayAppointments());
        promises.push(this.updateWeekAppointments());
        return Promise.all(promises);
    }

    @Watch('user', {immediate: true})
    guardPage(): void {
        if (!this.user) {
            this.$router.push({
                name: 'Landing',
            }).catch(() => {});
        }
    }

    setDisplayWeek(weeks: number): Promise<void> {
        this.monday = DateTime.fromJSDate(this.monday).plus({ weeks: weeks }).toJSDate();
        this.selectedDay = this.monday;
        return this.updateWeekAppointments();
    }

    selectDay(dow: number): void {
        // 0->6 with 0=sunday to 1-7 where 7=sunday
        let selectedDow = this.selectedDay.getDay();
        selectedDow = selectedDow == 0 ? 7 : selectedDow;
        if (selectedDow === dow) return;

        let selectedDay = DateTime.fromJSDate(this.selectedDay);
        if (selectedDow < dow) {
            selectedDay = selectedDay.plus({ days: dow - selectedDow });
        } else {
            selectedDay = selectedDay.minus({ days: selectedDow - dow });
        }

        this.selectedDay = selectedDay.toJSDate();
    }

    async updateTodayAppointments(): Promise<void> {
        let appointments = await this.getService().list(
            {
                from: {
                    year: this.today.getFullYear(),
                    month: this.today.getMonth() + 1,
                    day: this.today.getDate()
                },
                to: {
                    year: this.today.getFullYear(),
                    month: this.today.getMonth() + 1,
                    day: this.today.getDate()
                }
            }
        );
        if (!(appointments instanceof APIError)) {
            this.todayAppointments = appointments;
        } else {
            this.todayAppointments = [];
        }
    }

    async updateWeekAppointments(){
        let week = DateTime.fromJSDate(this.selectedDay).plus({ days: 6 }).toJSDate();

        let appointments = await this.getService().list(
            {
                from: {
                    year: this.selectedDay.getFullYear(),
                    month: this.selectedDay.getMonth() + 1,
                    day: this.selectedDay.getDate()
                },
                to: {
                    year: week.getFullYear(),
                    month: week.getMonth() + 1,
                    day: week.getDate()
                }
            }
        );

        let weekAppointments: Appointment[][] = [[],[],[],[],[],[],[]];

        if (!(appointments instanceof APIError)) {
            for (let appointment of appointments) {
                weekAppointments[appointment.dateFrom.getDay()].push(appointment);
            }
            this.weekAppointments = weekAppointments;
        } else {
            this.weekAppointments = weekAppointments;
        }
    }

    timeWithZero(time: number): string {
        if (time < 10) {
            return '0' + time;
        } else {
            return time.toString();
        }
    }

    getApiUrl(url:string):string{
        return createApiPath(url);
    }

    getMpdMonthOfYear(i: number): string {
        // @ts-ignore
        switch (this.monday.plusDays(i).getMonth()) {
            case 0:
                return this.$t('JanuaryAbbreviated').toString();
            case 1:
                return this.$t('FebruaryAbbreviated').toString();
            case 2:
                return this.$t('MarchAbbreviated').toString();
            case 3:
                return this.$t('AprilAbbreviated').toString();
            case 4:
                return this.$t('MayAbbreviated').toString();
            case 5:
                return this.$t('JuneAbbreviated').toString();
            case 6:
                return this.$t('JulyAbbreviated').toString();
            case 7:
                return this.$t('AugustAbbreviated').toString();
            case 8:
                return this.$t('SeptemberAbbreviated').toString();
            case 9:
                return this.$t('OctoberAbbreviated').toString();
            case 10:
                return this.$t('NovemberAbbreviated').toString();
            case 11:
                return this.$t('DecemberAbbreviated').toString();
            default:
                // @ts-ignore
                return this.monday.plusDays(i).getMonth();
        }
    }

    getMonday(day:Date):Date {
        // get day of week
        let toAdd = day.getDay();

        //remove the amount of days necessary to get to monday
        //(monday is 1, sunday is 0)
        toAdd = toAdd == 0 ? -6 : -1 * ( toAdd - 1 );
        
        //@ts-ignore
        return day.plusDays( toAdd );
    }

    async cancelAppointment(id: number, dow: number): Promise<void> {
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
                this.deleteAppointment(id, dow);
            }
            Vue.delete(this.beingCanceled,id);
        }
    }

    private deleteAppointment(id: number, dow: number): void {
        let index = this.weekAppointments[dow].findIndex(value => value.id == id);
        if (index > -1)
            this.weekAppointments[dow].splice(index, 1);

        index = this.todayAppointments.findIndex(value => value.id == id);
        if (index >= 0)
            this.todayAppointments.splice(index, 1);
    }

    private getService(): AppointmentService {
        return this.$container.get<AppointmentService>(TYPES.Services.AppointmentService);
    }
}
</script>

<style scoped>
.turno-item {
    border-radius: 2em !important;
    background-color: rgb(214, 214, 214);
}

.turno-list {
    -ms-overflow-style: none;
    scrollbar-width: none;
    height: 59vh;
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

.medicare-day-span {
    cursor: pointer;
}
</style>