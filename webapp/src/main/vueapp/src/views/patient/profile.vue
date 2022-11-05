<template>
    <div class="container flex-fill mx-5 pl-5 mt-3 w-100">
        <div class="row">
            <div class="col-4 align-items-start d-flex flex-column">
                <div class="picture-container no-select">
                    <div class="w-100 d-flex flex-column justify-content-center">
                        <div class="profile-picture-container">
                            <div style="margin-top: 100%;"></div>
                            <img
                                id="profilePic"
                                class="profile-picture rounded-circle"
                                :src="profilePicUrl"
                                alt="profile pic"
                            />
                        </div>
                    </div>
                    <div v-if="!uploadingProfilePic" @click="triggerChangePPInput" class="picture-overlay d-flex flex-column align-items-center justify-content-center pb-3">
                        <input ref="PPInput" @change="changeProfilePic" id="profile-picture-input" style="display: none;" type="file" accept="image/*">
                        <b-icon class="edit-pencil-icon" icon="pencil"/>
                    </div>
                    <div v-else class="spinner-picture-overlay d-flex flex-column align-items-center justify-content-center pb-3">
                        <b-spinner variant="success"></b-spinner>
                    </div>
                </div>
            </div>
            <div class="col-6">
                <div class="container p-0 pt-4 m-0">
                    <form @submit="submitForm">
                        <div class="row">
                            <div class="col p-0 m-0">
                                <h3>{{ $t('Name') }}
                                    <label for="fname" class="toggle-readonly">
                                        <img type="button" :src='editPencil' alt="editar" @click="enableFirstnameMod"/>
                                    </label>
                                </h3>
                                <input class="form-control mb-3 w-75" id="fname" name="fname" type="text" autocomplete="fname"
                                       v-model="firstname" :readonly="!firstnameModEnabled" :disabled="!firstnameModEnabled"/>
                                <!-- TODO: maybe expand feedback-->
                                <!-- TODO: check the i18n-->
                                <b-form-invalid-feedback :state="validFirstname">{{$t("Size.signupForm.firstName",[0,maxFirstnameLength,minFirstnameLength])}}</b-form-invalid-feedback>
                            </div>
                            <div class="col p-0 m-0">
                                <!-- TODO Connect image function-->
                                <h3>{{ $t('Surname') }}
                                    <label for="lname" class="toggle-readonly">
                                        <img type="button" :src='editPencil' alt="editar" @click="enableSurnameMod"/>
                                    </label>
                                </h3>
                                <input class="form-control mb-3 w-75" name="lname" id="lname" v-model="surname" type="text" autocomplete="lname"
                                       :readonly="!surnameModEnabled" :disabled="!surnameModEnabled"/>
                                <!-- TODO: maybe expand feedback-->
                                <!-- TODO: check the i18n-->
                                <b-form-invalid-feedback :state="validSurname">{{$t("Size.signupForm.surname",[0,maxSurnameLength,minSurnameLength])}}</b-form-invalid-feedback>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col p-0 m-0">
                                <h3>{{ $t('Phone') }}
                                    <label for="phone" class="toggle-readonly">
                                        <img type="button" :src='editPencil' alt="editar" @click="enablePhoneMod">
                                    </label>
                                </h3>
                                <input class="form-control mb-3 w-75" id="phone" name="phone" v-model="phone" type="text" autocomplete="phone"
                                       :readonly="!phoneModEnabled" :disabled="!phoneModEnabled"/>
                                <!-- TODO: if validation in phone then feedback should be provided probably -->
                            </div>
                            <div class="col p-0 m-0">
                                <h3>{{ $t('Email') }}
                                    <label for="email" class="toggle-readonly">
                                        <img type="button" :src='editPencil' alt="editar" @click="enableEmailMod"/>
                                    </label>
                                </h3>
                                <input class="form-control mb-3 w-75" id="email" name="email" v-model="email" type="text" autocomplete="username"
                                       :readonly="!emailModEnabled" :disabled="!emailModEnabled"/>
                                <!-- TODO: maybe expand feedback-->
                                <!-- TODO: check the i18n-->
                                <b-form-invalid-feedback :state="validEmail">{{$t("Email.signupForm.email")}}</b-form-invalid-feedback>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col p-0 m-0">
                                <h3>{{ $t('Password') }}
                                    <label for="password" class="toggle-readonly">
                                        <img type="button" :src='editPencil' alt="editar" @click="enablePasswordMod"/>
                                    </label>
                                </h3>
                                <input :type='passwordVis? "text": "password"' class="form-control mb-3 w-75" id="password" name="password" autocomplete="new-password"
                                       :readonly="!passwordModEnabled" :disabled="!passwordModEnabled"
                                       v-model="password"
                                />
                                <label for="password" class="toggle-visibility">
                                    <img type="button" :src='eye' v-if="!passwordVis && passwordModEnabled" alt="not visible password" @click="passwordVis=true">
                                    <img type="button" :src='noeye' v-else-if="passwordModEnabled" alt="visible password" @click="passwordVis=false">
                                </label>
                                <!-- TODO: add feedback -->
                                <!-- the problem lies in the fact that is you add a feedback the eyeicon gets missedplaced -->
                            </div>
                            <div v-if="passwordModEnabled" class="col p-0 m-0" id="repeat-password-container">
                                <h3>
                                    {{ $t('RepeatPassword') }}
                                    <label for="repeatPassword" class="toggle-readonly">
                                        <img :src='editPencil' alt="editar"/>
                                    </label>    
                                </h3>
                                <input :type='repeatPasswordVis? "text":"password"' class="form-control mb-3 w-75" name="new-password" autocomplete="new-password"
                                       id="repeatPassword"
                                       v-model="repeatPassword"
                                />
                                <label for="repeatPassword" class="toggle-visibility">
                                    <img type="button" :src='eye' v-if="!repeatPasswordVis" alt="not visible password" @click="repeatPasswordVis=true">
                                    <img type="button" :src='noeye' v-else alt="visible password" @click="repeatPasswordVis=false">
                                </label>
                                <!-- TODO: add feedback -->
                                <!-- the problem lies in the fact that is you add a feedback the eyeicon gets missedplaced -->
                            </div>
                        </div>
                        <div class="row justify-content-center align-items-end mt-2">
                            <button type="submit" class="btn btn-info">{{ $t('ConfirmChanges') }}</button>
                        </div>
                    </form>
                    <div class="col-2">
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import noeye from '@/assets/noeye.svg';
import eye from '@/assets/eye.svg';
import editPencil from '@/assets/editPencil.svg';
import {Component, Vue, Watch} from 'vue-property-decorator';
import {User} from '~/logic/models/User';

import {createApiPath, isValidEmail, Nullable} from '~/logic/Utils';
import defaultProfilePic from "@/assets/defaultProfilePic.svg";
import {State} from 'vuex-class';
import {userActionTypes, userMutationTypes} from '~/store/types/user.types';
import {UpdateUser} from '~/logic/interfaces/services/UserService';

@Component
export default class PatientProfile extends Vue {
    private noeye = noeye;
    private eye = eye;
    private editPencil = editPencil;
    private readonly defaultProfilePic = defaultProfilePic;

    private passwordVis = false;
    private repeatPasswordVis = false;

    private uploadingProfilePic = false;

    @State(state => state.users.profilePictureTimestamp)
    private timestamp: Nullable<number>;
    @State(state => state.auth.user)
    private readonly user: User;

    //enable/disable readonly property of input
    private firstnameModEnabled = false;
    enableFirstnameMod():void{this.firstnameModEnabled=true};
    private surnameModEnabled = false;
    enableSurnameMod():void{this.surnameModEnabled=true};
    private phoneModEnabled = false;
    enablePhoneMod():void{this.phoneModEnabled=true};
    private emailModEnabled = false;
    enableEmailMod():void{this.emailModEnabled=true};
    private passwordModEnabled = false;
    enablePasswordMod():void{this.passwordModEnabled=true};

    get profilePicUrl(): string {
        let ts;
        if (this.timestamp)
            ts = `?ts=${this.timestamp}`;
        else
            ts = '';

        return this.getApiUrl(`/users/${this.user.id}/picture${ts}`) || defaultProfilePic;
    }

    @Watch('user', {immediate: true})
    guardPage(): void {
        if (!this.user) {
            this.$router.push({
                name: 'Landing',
            }).catch(() => {});
        }
    }

    mounted(){
        let user = this.$store.state.auth.user;
        this.firstname = user.firstName||"";
        this.surname = user.surname||"";
        this.email = user.email||"";
        this.phone = user.phone||"";
    }

    getApiUrl(url:string):string{
        return createApiPath(url);
    }

    changeProfilePic(e:InputEvent){
        //@ts-ignore
        if(!e.target.files||e.target.files.length<1){
            return;
        }
        //get profile pic file and check type
        //@ts-ignore
        let file = e.target.files[0];
        if (!file.type.includes("image")) {
            //TODO: toast error
            this.showErrorToast('Error');
            return;
        }
        let formData = new FormData();
        formData.append('picture', file);
        this.uploadingProfilePic = true;
        fetch(this.getApiUrl(`/users/${this.user.id}/picture`), {
            method: "POST",
            body: formData
        }).then((r) => {
            if (r.ok) {
                this.$store.commit('users/updateProfilePictureTimestamp', userMutationTypes.updateProfilePictureTimestamp());
                //TODO:show ok toast
            }
        }).catch((e) => {
            //TODO:show error message
        }).finally(()=>{
            this.uploadingProfilePic = false;
        });
    }

    triggerChangePPInput(){
        //@ts-ignore
        this.$refs.PPInput.click();
    }

    showErrorToast(message:string){
        this.$bvToast.toast(message,{
            title:this.$t("ThereWasAnError").toString(),
            variant:"danger"
        })
    }

    //-------------------------Form------------------------------

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
    private phone:string = "";
    private password:string = "";
    private repeatPassword:string = "";

    get validFirstname():boolean {
        return  (this.firstname.length>=this.minFirstnameLength
                && this.firstname.length<=this.maxFirstnameLength)
                || !this.firstnameModEnabled;
    }

    get validSurname():boolean {
        return  (this.surname.length>=this.minSurnameLength
                && this.surname.length<=this.maxSurnameLength)
                || !this.surnameModEnabled;
    }
    get validEmail():boolean {
        return  isValidEmail(this.email)
                || !this.emailModEnabled;
    }
    get validPassword():boolean {
        return (this.password.length>=this.minPasswordLength
                && this.password.length<=this.maxPasswordLength)
                || !this.passwordModEnabled;
    }
    get validRepeatPassword():boolean {
        return  (this.password === this.repeatPassword)
                || !this.passwordModEnabled;
    }

    get validPhone():boolean{
        return  (this.phone.length >= 7 && this.phone.length <= 14)
                || !this.phoneModEnabled; // Algunos lugares tienen tels con 7 dig
    }

    get validUserUpdate(): boolean {
        return  this.validFirstname && this.validSurname && this.validEmail &&
                this.validPassword && this.validRepeatPassword && this.validPhone;
    }

    submitForm(e:any): void{
        e.preventDefault();
        let updateUser:UpdateUser = {};
        if(this.firstnameModEnabled)updateUser.firstName = this.firstname;
        if(this.surnameModEnabled)updateUser.surname = this.surname;
        if(this.phoneModEnabled)updateUser.phone = this.phone;
        if(this.emailModEnabled)updateUser.email = this.email;
        if(this.passwordModEnabled)updateUser.password = this.password;

        if(this.validUserUpdate){
            this.$store.dispatch('users/updateUser', userActionTypes.updateUser(updateUser));
        }else{
            this.showErrorToast(this.$t('ErrorC1').toString());
        }
    }

}
</script>

<style scoped>
.toggle-visibility{
    position: absolute;
    bottom: 1em;
    right: 5em;
}
html, body {
    height: 100%;
}

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

.form-control[disabled] {
    background-color: rgb(214, 214, 214);
}
.form-control {
    background-color: #f0f0f0;
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

.picture-container {
    position: relative;
    height: 10em;
    width: 10em;
    border-radius: 10em;
    cursor: pointer;
}

.picture-container > * {
    position: absolute;
    top: 0;
    left: 0;
}

.picture-container .picture-overlay {
    z-index: 1;
    width: 100%;
    height: 100%;
    border-radius: 10em;
    opacity: 0;
    transition: opacity 0.1s ease-in-out 0s;
}

.picture-container:hover .picture-overlay,
.picture-container:active .picture-overlay {
    opacity: 100%;
}

.picture-container .picture-overlay > .edit-pencil-icon {
    padding: 10px;
    background: #333333bb;
    border-radius: 50px;
    font-size: 3rem;
    color: #ffffff;
    cursor: pointer;
}

.picture-container .spinner-picture-overlay {
    z-index: 1;
    width: 100%;
    height: 100%;
}

#profilePic {
    height: 100%;
    border-radius: 10em;
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