<template>
  <span id="app">
    <transition name="slide" mode="out-in">
        <Navbar v-if="!hideNav"/>
    </transition>
    <transition name="slide" mode="out-in">
        <RouterView/>
    </transition>
  </span>
</template>

<script lang="ts">
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import Navbar from "./components/navbar/navbar.vue";
import {Component, Vue} from 'vue-property-decorator';
import {userActionTypes} from '~/store/types/user.types';

import { getErrorMessage } from "@/logic/Utils";
import {APIErrorCallback, APIErrorEventName} from '~/logic/interfaces/APIErrorEvent';
import EventBus from '~/logic/EventBus';
import {APIError} from '~/logic/models/APIError';

@Component({
    components: {
        Navbar
    }
})
export default class App extends Vue {
    get hideNav(): boolean {
        return this.$route.meta.hideNav;
    }

    mounted() {
        EventBus.$on(APIErrorEventName, this.apiErrorCallback as APIErrorCallback);
    }

    beforeDestroy() {
        // Clean up listener
        EventBus.$off(APIErrorEventName, this.apiErrorCallback as APIErrorCallback);
    }

    showErrorToast(code:number){
        this.$bvToast.toast(getErrorMessage(code),{
            title:this.$t("ThereWasAnError").toString(),
            variant:"danger"
        })
    }

    private apiErrorCallback(code: number): void {
        this.showErrorToast(code);
    }
}
</script>

<style>
.slide-enter-active,
.slide-leave-active{
    transition: opacity .5s,transform .5s;
}
.slide-enter{
    opacity: 0;
    transform: translateX(50%);
}
.slide-leave-to{
    opacity: 0;
    transform: translateX(-50%);
}
</style>
