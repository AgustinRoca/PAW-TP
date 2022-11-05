<template>
    <b-modal    v-model="showModal" 
                :hide-footer="true"
                :hide-header="true"
                :no-fade="true"
                body-class="p-0"
                content-class="p-0"
                @show="cleanValues"
                #default="{ok,cancel}">
        <form class="addturn-form border p-5 rounded" @submit.prevent="submitForm(ok)">
            <div class="row">
                <h6>Medicare <img :src='logo' id="logo" alt="logo"/></h6>
            </div>
            <div class="row justify-content-start">
                <h1 class="addturn-form-title">
                    {{$t("AddSchedule")}}
                </h1>
            </div>

            <div class="form-group row">
                <div class="col">
                    <label>
                        {{$t("DayOfWeek")}}
                    </label>
                </div>
                <div class="col-8">
                    <b-form-group>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="0">{{$t("Monday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="1">{{$t("Tuesday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="2">{{$t("Wednesday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="3">{{$t("Thursday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="4">{{$t("Friday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="5">{{$t("Saturday")}}</b-form-radio>
                        <b-form-radio v-model="dowSelected" name="some-radios" :value="6">{{$t("Sunday")}}</b-form-radio>
                    </b-form-group>
                </div>
            </div>

            <div class="form-group row">
                <div class="col">
                    <label for="startHour">
                        {{$t("StartingHour")}}
                    </label>
                </div>
                <div class="col-8">
                    <input v-model="startHour" class="form-control" type="time" name="startHour" id="startHour" />
                </div>
            </div>

            <div class="form-group row">
                <div class="col">
                    <label for="endHour">
                        {{$t("FinishingHour")}}
                    </label>
                </div>
                <div class="col-8">
                    <input v-model="endHour" class="form-control" type="time" name="endHour" id="endHour" />
                </div>
            </div>

            <div class="form-group row">
                <div class="col">
                    <label for="officeId">
                        {{$t("Office")}}
                    </label>
                </div>
                <div class="col-8">
                    <select v-model="officeSelected" class="form-control" name="officeId" id="officeId" >
                        <option v-for="doctor in doctors" :key="doctor.office.id" :value="doctor.office.id">
                            {{doctor.office.name}}
                        </option>
                    </select>
                </div>
            </div>

            <div class="form-row justify-content-between">
                <button class="form-atras-btn btn" type="button" @click="cancel()">
                    {{$t("Back")}}
                </button>
                <button type="submit" class="btn btn-primary" :disabled="disabledButton">
                    {{$t("Add")}}
                </button>
            </div>
        </form>
    </b-modal>    
</template>
<script lang="ts">
import {Component, VModel, Vue, Watch} from 'vue-property-decorator';
import logo from "@/assets/logo.svg";
import {Nullable} from '@/logic/Utils';
import {Doctor} from '~/logic/models/Doctor';
import {State} from 'vuex-class';
import TYPES from '~/logic/types';
import {WorkdayService} from '~/logic/interfaces/services/WorkdayService';
import {Day, WorkdayTime} from '~/logic/models/utils/DateRange';
import {APIError} from '~/logic/models/APIError';

@Component
export default class AddWorkday extends Vue {
    private readonly logo = logo;

    @VModel({type:Boolean,default:true})
    private showModal:boolean;

    private dowSelected = 0;
    private startHour = "";
    private endHour = "";
    //this is selected on mount and on clean values
    private officeSelected = 0;
    private disabledButton: boolean = true;

    @State(state => state.auth.doctors)
    private readonly doctors: Doctor[];

    mounted(){
        if(this.doctors&&this.doctors[0]&&this.doctors[0].office&&this.doctors[0].office.id){
            this.officeSelected = this.doctors[0].office.id
        }
    }

    private cleanValues(){
        this.dowSelected = 0;
        this.startHour = "";
        this.endHour = "";
        if(this.doctors&&this.doctors[0]&&this.doctors[0].office&&this.doctors[0].office.id){
            this.officeSelected = this.doctors[0].office.id;
        }
    }

    submitForm(okCallback: Function) {
        let start = this.getWorkdayTime(this.startHour);
        let end = this.getWorkdayTime(this.endHour);
        if (!start || !end) return;

        let day = [ 'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'][this.dowSelected];
        this.getWorkdayService().createList([{
            day: day as Day,
            end: end,
            start: start
        }]).then((value) => {
            if (!(value instanceof APIError)) {
                okCallback();
                this.$emit('added');
            }
        });
    }

    @Watch('startHour')
    @Watch('endHour')
    private setDisabledButton(): void {
        let start = this.getWorkdayTime(this.startHour);
        let end = this.getWorkdayTime(this.endHour);
        console.log(start);
        console.log(end);
        this.disabledButton = !start || !end;
    }

    private getWorkdayTime(rawString: string): Nullable<WorkdayTime> {
        let time = rawString.split(":");
        try {
            let value = {
                hour: Number.parseInt(time[0]),
                minute: Number.parseInt(time[1])
            };
            if (isNaN(value.hour) || isNaN(value.minute))
                return null;
            return value;
        } catch (e) {
            return null;
        }
    }

    private getWorkdayService(): WorkdayService {
        return this.$container.get(TYPES.Services.WorkdayService);
    }
}
</script>

<style scoped>
body, html {
    height: 100%;
    background-color: rgba(0, 196, 186, 0.205);
}

.addturn-form {
    background-color: #fff;
    border-radius: 1em !important;
    box-shadow: 10px 9px 12px 0px rgba(0, 196, 186, 0.205);
}


.form-link:hover {
    text-decoration: none;
}

.addturn-form input, .addturn-form select {
    background-color: #f0f0f0;
}

.addturn-form input:focus, .addturn-form select:focus {
    background-color: #e0e0e0;
}

.addturn-form-title {
    margin-bottom: 1em;
}

.addturn-form #logo {
    width: 1em;
}

.addturn-form button {
    background-color: #00C4BA;
    color: white;
}

.addturn-form button:hover {
    background-color: rgb(1, 150, 142);
    color: #fafafa;
}

.form-atras-btn {
    background-color: grey !important;
    color: white;
}

.form-atras-btn:hover {
    background-color: rgb(94, 94, 94) !important;
    color: white;
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