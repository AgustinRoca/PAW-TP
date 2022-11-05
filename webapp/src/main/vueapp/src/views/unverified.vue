<template>
    <div class="container flex-grow-1 d-flex flex-column w-100 h-100 justify-content-center align-items-center">
        <div class="px-5 py-3 d-flex flex-column align-items-center justify-content-center styled-container">
            <h1 class="mb-4">{{ $t('MissingEmailVerification') }}</h1>
            <h4>{{ $t('VerificationEmailSent') }}:</h4>
            <h4>"{{ email }}"</h4>
            <h4>{{ $t('UntilVerifiedCantContinue') }}</h4>
        </div>
        <div v-if="tokenError" class="alert alert-danger medicare-alert-error">
            {{ $t('TokenError.loginForm') }}
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Vue, Watch} from 'vue-property-decorator';
import TYPES from '~/logic/types';
import {VerifyService} from '~/logic/interfaces/services/VerifyService';
import {State} from 'vuex-class';
import {User} from '~/logic/models/User';
import {createPath} from "~/logic/Utils";

@Component
export default class Unverified extends Vue {
    @State(state => state.auth.user)
    private readonly user:User;

    private email = "";

    private tokenError: boolean = false;

    @Watch("user")
    userChange(){
        if(!this.user || this.user.verified){
            this.$router.push({
                path:this.getUrl("/")
            }).catch(()=>{});
        }else{
            this.email = this.user.email;
        }
    }

    beforeMount(){
        this.email = this.user.email
    }

    getUrl(url:string){
        return createPath(url);
    }

    async mounted(): Promise<void> {
        let token: string = '';
        if (typeof this.$route.params !== 'object'
            || typeof this.$route.params.token !== 'string'
            || (token = this.$route.params.token).length === 0)
        {
            await this.$store.dispatch("auth/reload");
            return;
        }

        let service: VerifyService = this.$container.get(TYPES.Services.VerifyService);
        let response = await service.verify(token);

        if (typeof response === 'boolean') {
            this.tokenError = !response;
        } else {
            this.tokenError = true;
        }
    }
}
</script>

<style>

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

.form-control {
    background-color: rgba(214, 214, 214);
}

.turno-item {
    border-radius: 2em !important;
    background-color: rgba(214, 214, 214);
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

.white-text {
    color: white !important;
}

.styled-container {
    background-color: rgba(150, 149, 149, 0.404);
    border-radius: 1em;
}

.medicare-alert-error {
    position: fixed;
    top: 5em;
    right: .5em;
}
</style>