<template>
    <div class="container w-100 justify-content-start">
        <RouterLink :to='getUrl("")' class="navbar-brand header-brand header-a-element">
            <img class="navbar-brand" :src='logo' alt="logo" id="navbar-logo">
            MediCare
        </RouterLink>
    </div>
</template>

<script lang="ts">
import logo from '@/assets/whiteLogo.svg';
import {Component, Vue} from 'vue-property-decorator';

import {createPath} from "~/logic/Utils";

@Component
export default class NavbarLogo extends Vue {
    private readonly logo = logo;

    getUrl(url:string):string{
        return createPath(url);
    }
}
</script>

<style scoped>
#navbar-logo{
    height: 2em;
}
</style>