<template>
    <b-modal    v-model="showModal" 
                :hide-footer="true"
                :hide-header="true"
                :no-fade="true"
                body-class="p-0"
                content-class="p-0"
                @show="cleanValues"
                #default="{ok,cancel}">
        <form class="addturn-form border p-5 rounded" @submit="submitForm">
            <div class="row">
                <h6>Medicare <img :src='logo' id="logo" alt="logo"/></h6>
            </div>
            <div class="row justify-content-start">
                <h1 class="addturn-form-title">
                    {{$t("AddSpecialty")}}
                </h1>
            </div>

            <div class="form-group row">
                <div class="col">
                    <label for="specialtyId">
                        {{$t("Specialty")}}
                    </label>
                </div>
                <div class="col-8">
                    <select class="form-control" name="specialtyId" id="specialtyId" path="specialtyId" v-model="selectedSpecialtyId">
                        <option :value="null" disabled selected>{{ $t('Specialty') }}</option>
                        <option v-for="specialty in specialties" :key="specialty.id"
                                :value="specialty.id">{{ specialty.name }}</option>
                    </select>
                </div>
            </div>

            <div class="form-row justify-content-between">
                <button type="button" class="form-atras-btn btn" @click="cancel()">
                    {{$t("Back")}}
                </button>
                <button type="submit" class="btn btn-primary" @click="ok()">
                    {{$t("Add")}}
                </button>
            </div>
        </form>
    </b-modal>
</template>

<script lang="ts">
import {Component, Prop, VModel, Vue} from 'vue-property-decorator';
import logo from "@/assets/logo.svg";
import {Nullable} from '@/logic/Utils';
import {State} from 'vuex-class';
import {doctorActionTypes} from '~/store/types/doctor.types';
import {Doctor} from '~/logic/models/Doctor';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';

@Component
export default class AddSpecialty extends Vue {
    private readonly logo = logo;
    @VModel({type:Boolean,default:true})
    private showModal: boolean;
    private selectedSpecialtyId: Nullable<number> = null;
    @Prop({ type: Array, required: true })
    private readonly specialties: DoctorSpecialty[];
    @State(state => state.auth.doctors)
    private readonly doctors: Doctor[];

    private cleanValues(){
        this.selectedSpecialtyId = null;
    }

    public async submitForm(e:Event): Promise<void> {
        e.stopPropagation();
        e.preventDefault();

        if (!this.selectedSpecialtyId) return;

        for (let doctor of this.doctors) {
            let specialties = doctor.specialtyIds.map(value => value);
            specialties.push(this.selectedSpecialtyId);
            await this.$store.dispatch('doctors/updateDoctor', doctorActionTypes.updateDoctor({
                id: doctor.id,
                doctor: {
                    email: doctor.email,
                    phone: doctor.phone,
                    specialtyIds: specialties
                }
            }));
        }
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