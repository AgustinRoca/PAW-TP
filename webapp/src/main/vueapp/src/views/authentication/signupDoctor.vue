<template>
    <div
        class="container-fluid w-100 m-0 p-0 d-flex flex-column justify-content-center align-items-center signup-container">
        <form class="register-form border my-3 p-5 rounded" @submit="submitForm">
            <div class="row">
                <h6>Medicare <img :src='logo' id="logo" alt="logo"/></h6>
            </div>
            <div class="row justify-content-start">
                <h1 class="register-form-title">{{ $t('CreateAccount') }}</h1>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="fname">{{ $t('Name') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="firstname" class="form-control" type="text" name="fname" id="fname" autocomplete="fname"/>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validFirstname">{{$t("Size.signupForm.firstName",[0,maxFirstnameLength,minFirstnameLength])}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="lname">{{ $t('Surname') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="surname" class="form-control" type="text" name="lname" id="lname" autocomplete="lname"/>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validSurname">{{$t("Size.signupForm.surname",[0,maxSurnameLength,minSurnameLength])}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="email">{{ $t('Email') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="email" class="form-control" type="email" name="email" id="email" autocomplete="username"/>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validEmail">{{$t("Email.signupForm.email")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="medicare_password">{{ $t('Password') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="password" class="form-control pr-5"
                           :type='showPassword?"text":"password"' name="new-password" autocomplete="new-password"
                           id="medicare_password"/>
                    <label for="medicare_password" class="toggle-visibility" @click="toggleShowPassword()">
                        <img v-if="!showPassword" :src='eye'>
                        <img v-else :src='noeye'>
                    </label>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validPassword">{{$t("Size.signupForm.password",[0,maxPasswordLength,minPasswordLength])}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="medicare_repeatPassword">{{ $t('RepeatPassword') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="repeatPassword" class="form-control pr-5"
                           :type='showRepeatPassword?"text":"password"' name="new-password" autocomplete="new-password"
                           id="medicare_repeatPassword"/>
                    <label for="medicare_repeatPassword" class="toggle-visibility" @click="toggleShowRepeatPassword()">
                        <img v-if="!showRepeatPassword" :src='eye'>
                        <img v-else :src='noeye'>
                    </label>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validRepeatPassword">{{$t("Equals.signupForm.repeatPassword")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="country">{{ $t('Country') }}</label>
                </div>
                <div class="col-8">
                    <select v-model="country" class="form-control" style="width: 100%;" items="${countryMap}" id="country">
                        <option v-for="country in countries" :key="country.id" :value="country.id">
                            {{ country.name }}
                        </option>
                    </select>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validCountry">{{$t("NotNull.signupForm.countryId")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div v-show="validCountry" class="form-group row" id="province-container">
                <div class="col">
                    <label for="province">{{ $t('Province') }}</label>
                </div>
                <div class="col-8">
                    <select v-model="province" class="form-control" style="width: 100%;" id="province">
                        <option v-for="province in provinces" :key="province.id" :value="province.id">
                            {{ province.name }}
                        </option>
                    </select>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validProvince">{{$t("NotNull.signupForm.provinceId")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div v-show="validProvince" class="form-group row" id="locality-container">
                <div class="col">
                    <label for="locality">{{ $t('Locality') }}</label>
                </div>
                <div class="col-8">
                    <select v-model="locality" class="form-control" style="width: 100%;" id="locality">
                        <option v-for="locality in localities" :key="locality.id" :value="locality.id">
                            {{ locality.name }}
                        </option>
                    </select>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validLocality">{{$t("NotNull.signupForm.localityId")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div v-show="validLocality" class="form-group row">
                <div class="col">
                    <label for="address">{{ $t('Address') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="address" class="form-control" type="text" name="address" id="address"/>
                    <!-- TODO: maybe expand feedback-->
                    <!-- TODO: check the i18n-->
                    <b-form-invalid-feedback :state="validAddress">{{$t("NotEmpty.signupForm.address")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-row justify-content-between align-items-end mt-2">
                <RouterLink class="form-link" :to='getUrl("login")'>{{ $t('Login') }}</RouterLink>
                <button type="submit" class="btn btn-primary">{{ $t('Confirm') }}</button>
            </div>
        </form>
    </div>
</template>

<script lang="ts">
import eye from '@/assets/eye.svg';
import noeye from '@/assets/noeye.svg';
import logo from '@/assets/logo.svg';
import {Component, Vue, Watch} from 'vue-property-decorator';

import {createPath, ID, isValidEmail, Nullable} from '~/logic/Utils';
import {userActionTypes} from '~/store/types/user.types';
import {Locality} from '~/logic/models/Locality';
import {State} from 'vuex-class';
import {countryActionTypes} from '~/store/types/countries.types';
import {provinceActionTypes} from '~/store/types/provinces.types';
import {Country} from '~/logic/models/Country';
import {Province} from '~/logic/models/Province';
import {LocalityService} from '~/logic/interfaces/services/LocalityService';
import TYPES from '~/logic/types';
import {APIError} from '~/logic/models/APIError';
import {User} from '~/logic/models/User';

@Component
export default class SignupDoctor extends Vue {
    private showPassword = false;
    private showRepeatPassword = false;
    private logo = logo;
    private eye = eye;
    private noeye = noeye;

    @State(state => state.countries.countries)
    private readonly countries: Country[];
    @State(state => state.provinces.provinces)
    private readonly provinces: Province[];

    private localities: Locality[] = [];

    private readonly minFirstnameLength = 2;
    private readonly maxFirstnameLength = 20;
    private readonly minSurnameLength = 2;
    private readonly maxSurnameLength = 20;
    private readonly minPasswordLength = 8;
    private readonly maxPasswordLength = 100;

    //form values
    private firstname:string = "";
    private surname:string = "";
    private email:string = "";
    private password:string = "";
    private repeatPassword:string = "";

    private country: Nullable<ID> = null;
    private province: Nullable<ID> = null;
    private locality: Nullable<number> = null;
    private address = "";

    @State(state => state.auth.user)
    private readonly user: Nullable<User>;
    @Watch('user')
    public goBack(): void {
        if(this.user && this.$route.params.prevto){
            this.$router.push(this.$route.params.prevto).catch(()=>{})
        }
        else if( this.user ){
                this.$router.push({
                    name: 'Landing',
                }).catch(()=>{});
            }
    }

    public mounted(): void {
        this.$store.dispatch('countries/loadCountries', countryActionTypes.loadCountries());
    }

    @Watch('country', {immediate: true})
    loadProvinces(): void {
        if (!this.country) return;

        this.$store.dispatch('provinces/loadProvinces', provinceActionTypes.loadProvinces({
            countryId: this.country!
        }));
    }

    @Watch('province', {immediate: true})
    async loadLocalities(): Promise<void> {
        if (!this.province) return;

        let localities = await this.$container.get<LocalityService>(TYPES.Services.LocalityService)
            .list({
                provinceId: this.province!,
                countryId: this.country!
            });
        if (!(localities instanceof APIError)) {
            this.localities = localities || [];
        }
    }

    toggleShowPassword(): void {
        this.showPassword = !this.showPassword;
    }

    toggleShowRepeatPassword(): void {
        this.showRepeatPassword = !this.showRepeatPassword;
    }

    get validFirstname():boolean {
        return  this.firstname.length>=this.minFirstnameLength 
                && this.firstname.length<=this.maxFirstnameLength;
    }

    get validSurname():boolean {
        return  this.surname.length>=this.minSurnameLength 
                && this.surname.length<=this.maxSurnameLength;
    }
    get validEmail():boolean {
        return isValidEmail(this.email);
    }
    get validPassword():boolean {
        return this.password.length>=this.minPasswordLength 
                && this.password.length<=this.maxPasswordLength;
    }
    get validRepeatPassword():boolean {
        return this.password === this.repeatPassword;
    }
    get validCountry():boolean{
        return this.country != null;
    }
    get validProvince():boolean{
        return this.province != null;
    }
    get validLocality():boolean{
        return this.locality != null;
    }
    get validAddress():boolean{
        return this.address.length!=0;
    }

    getUrl(url:string):string{
        return createPath(url);
    }

    get valid(): boolean {
        return  this.validFirstname && this.validSurname && this.validEmail &&
                this.validPassword && this.validRepeatPassword &&
                this.validCountry && this.validProvince &&
                this.validLocality && this.validAddress;
    }

    public submitForm(e: Event): void {
        e.preventDefault();
        e.stopPropagation();
        if(this.valid){
            this.$store.dispatch('users/createAsDoctor', userActionTypes.createAsDoctor({
                doctor: {
                    email: this.email,
                    firstName: this.firstname,
                    password: this.password,
                    surname: this.surname,
                    specialtyIds: [],
                    localityId: this.locality!,
                    address: this.address
                }
            }));
        }
    }
}
</script>

<style scoped>
.signup-container {
    background-color: rgba(0, 196, 186, 0.205);
    min-height: 100%;
}

.register-form {
    background-color: #fff;
    border-radius: 1em !important;
    box-shadow: 10px 9px 12px 0px rgba(0, 196, 186, 0.205);
    box-sizing: border-box;
}


.form-link:hover {
    text-decoration: none;
}

.register-form input, .register-form select {
    background-color: #f0f0f0;
}

.register-form input:focus, .register-form select:focus {
    background-color: #e0e0e0;
}

.register-form-title {
    margin-bottom: 1em;
}

.register-form #logo {
    width: 1em;
}

.register-form button {
    background-color: #00C4BA;
    color: white;
}

.register-form button:hover {
    background-color: rgb(1, 150, 142);
    color: #fafafa;
}

.form-password {
    position: relative;
}

.toggle-visibility {
    position: absolute;
    right: 2em;
    top: 0.6em;
    bottom: auto;
    left: auto;
    z-index: 1;

    cursor: pointer;
}

.form-back-btn {
    background-color: grey !important;
    color: white;
}

.form-back-btn:hover {
    background-color: rgb(94, 94, 94) !important;
    color: white;
}

.card.card-shadow {
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
    transition: box-shadow 0.3s ease-in-out;
}

.card.card-shadow:hover {
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.pointer {
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