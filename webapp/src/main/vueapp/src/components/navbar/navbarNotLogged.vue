<template>
    <div class="container w-100 justify-content-end">
        <RouterLink :to='getUrl("signup/doctor")' class="header-a-element nav-link mx-3">{{ $t('AreYouMedic') }}
        </RouterLink>
        <RouterLink :to='getUrl("signup")' class="header-a-element nav-link mx-3">{{ $t('SignUp') }}</RouterLink>
        <RouterLink :to='getUrl("login")' class="header-btn-element btn btn-light rounded mx-3">{{ $t('Login') }}
        </RouterLink>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';

import {createPath} from "~/logic/Utils";

@Component
export default class NavbarNotLogged extends Vue {
    getUrl(url:string):string{
        return createPath(url);
    }
}
</script>