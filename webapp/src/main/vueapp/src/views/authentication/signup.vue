<template>
    <div
        class="container-fluid w-100 h-100 d-flex flex-column justify-content-center align-items-center signup-container">
        <div class="register-form border p-5 rounded">
            <div class="row">
                <h6>Medicare <img :src='logo' id="logo"/></h6>
            </div>
            <div class="row justify-content-start">
                <h1 class="register-form-title">{{ $t('CreateAccount') }}</h1>
            </div>
            <div class="form-row justify-content-between align-items-end">
                <RouterLink :to='getUrl("signup/patient")' tag="div"
                             class="d-flex flex-column mr-5 align-items-center card card-shadow pointer p-2"
                             id="signup-patient">
                    <img :src="hospitalUserIcon" style="height:80px;">
                    <h4 class="mt-2">{{ $t('LookingForMedics') }}</h4>
                </RouterLink>
                <RouterLink :to='getUrl("signup/doctor")' tag="div"
                             class="d-flex flex-column align-items-center card card-shadow pointer p-2"
                             id="signup-doctor">
                    <img :src="userMdIcon" style="height:80px;">
                    <h4 class="mt-2">{{ $t('IMAMedic') }}</h4>
                </RouterLink>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import logo from '@/assets/logo.svg';
import {Component, Vue} from 'vue-property-decorator';

/* this icons are from font awesome */
import hospitalUserIcon from "~/assets/hospitalUser.svg";
import userMdIcon from "~/assets/userMd.svg";

import {createPath} from "~/logic/Utils";

@Component
export default class Signup extends Vue {
    private logo = logo;
    private readonly hospitalUserIcon = hospitalUserIcon;
    private readonly userMdIcon = userMdIcon;

    getUrl(url:string):string{
        return createPath(url);
    }
}
</script>

<style scoped>
.signup-container {
    background-color: rgba(0, 196, 186, 0.205);
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