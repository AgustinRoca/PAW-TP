<template>
    <div
        class="container-fluid w-100 h-100 d-flex flex-column justify-content-center align-items-center login-container">
        <form class="register-form border p-5 rounded" @submit="login">
            <div class="row">
                <h6>Medicare <img :src='logo' id="logo"/></h6>
            </div>
            <div class="row justify-content-start">
                <h1 class="register-form-title">{{ $t('Login') }}</h1>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="medicare_email">{{ $t('Email') }}</label>
                </div>
                <div class="col-8">           
                    <b-input    v-model="email" class="form-control" type="email" name="current-email" autocomplete="username"
                                id="medicare_email" @focus="setShowInvalid"/>
                    <!-- TODO:maybe expand email feedback-->
                    <b-form-invalid-feedback v-if="showInvalid" :state="validEmail">{{$t("Email.signupForm.email")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row">
                <div class="col">
                    <label for="medicare_password">{{ $t('Password') }}</label>
                </div>
                <div class="col-8">
                    <b-input v-model="password" class="form-control pr-5" :type='showPassword?"text":"password"' name="current-password" autocomplete="current-password"
                           id="medicare_password" @focus="setShowInvalid"/>
                    <label for="medicare_password" class="toggle-visibility" @click="toggleShowPassword()">
                        <img v-if="!showPassword" :src='eye'>
                        <img v-else :src='noeye'>
                    </label>
                    <!-- TODO:maybe expand feedback -->
                    <b-form-invalid-feedback v-if="showInvalid" :state="validPassword">{{$t("NotEmpty.signupForm.password")}}</b-form-invalid-feedback>
                </div>
            </div>
            <div class="form-group row align-items-center">
                <div class="col">
                    <label for="medicare_remember_me" class="mb-0">{{ $t('RememberMe') }}</label>
                </div>
                <div class="col-8">
                    <input type="checkbox" id="medicare_remember_me" name="medicare_remember_me"/>
                </div>
            </div>
            <div class="form-row justify-content-between align-items-end mt-2">
                <RouterLink class="form-link" :to="getUrl('/signup')">{{ $t('CreateAccount') }}</RouterLink>
                <b-button
                    type="submit"
                    :disabled="disabledButton||loggingIn"
                    variant="primary"
                >
                    <span v-if="!loggingIn">{{ $t('Confirm') }}</span>
                    <span v-else>
                        <b-spinner small></b-spinner>
                    </span>
                </b-button>
            </div>
            <p v-if="invalidCredentials" class="mt-4 mb-0 text-danger">
                {{ $t('InvalidCredentials.loginForm') }}
            </p>
        </form>
    </div>
</template>

<script lang="ts">
import logo from '@/assets/logo.svg';
import eye from '@/assets/eye.svg';
import noeye from '@/assets/noeye.svg';
import {Component, Vue, Watch} from 'vue-property-decorator';
import {createPath, isValidEmail, Nullable} from '~/logic/Utils';
import {authActionTypes} from '~/store/types/auth.types';
import {State} from 'vuex-class';
import {User} from '~/logic/models/User';

@Component
export default class Login extends Vue {
    private logo = logo;
    private eye = eye;
    private noeye = noeye;
    private showPassword = false;
    private showInvalid = false;
    private invalidCredentials = false;
    private email = '';
    private password = '';
    @State(state => state.auth.user)
    private readonly user: Nullable<User>;
    @State(state => state.auth.loggingIn)
    private readonly loggingIn: boolean;


    get disabledButton(): boolean {
        return !this.valid;
    }

    beforeMount(){
        if(this.user){
            this.goBack();
        }
    }

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

    public toggleShowPassword(): void {
        this.showPassword = !this.showPassword;
    }

    public setShowInvalid():void{
        this.showInvalid = true;
    }

    public login(e: Event) {
        e.preventDefault();
        e.stopPropagation();
        if (this.valid){
            this.$store.dispatch('auth/login', authActionTypes.login({
                password: this.password,
                email: this.email
            }));
        }
    }

    get valid(): boolean {
        return this.validEmail && this.validPassword;
    }

    get validEmail():boolean{
        return isValidEmail(this.email.trim());
    }

    get validPassword():boolean{
        return this.password.length!=0;
    }

    getUrl(url:string):string{
        return createPath(url);
    }
}
</script>

<style scoped>
body, html {
    height: 100%;
}

.login-container {
    background-color: rgba(0, 196, 186, 0.205);
}

.register-form {
    background-color: #fff;
    border-radius: 1em !important;
    box-shadow: 10px 9px 12px 0px rgba(0, 196, 186, 0.205);
    max-width: 430px;
    width: 430px;
}


.form-link:hover {
    text-decoration: none;
}

.register-form input {
    background-color: #f0f0f0;
}

.register-form input:focus {
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