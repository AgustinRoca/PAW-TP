<template>
    <div class="container h-100 d-flex flex-column justify-content-center align-items-center">
        <div>
            <h1>MediCare<img :src="logo" alt="logo" class="logo-for-error"></h1>
            <h4><h4 class="d-inline">500</h4>.
                {{$t("ThereWasAnError")}}
                .
            </h4>
            <h6>
                {{$t("SomethingFailedOnOurServer")}}
                .
            </h6>
            <h6>
                {{$t("TryRefreshOrBackToHome")}}
            </h6>
            <RouterLink :to='getUrl("")' class="btn btn-secondary" id="homeBtn">
                {{$t("BackToHome")}}
            </RouterLink>
        </div>
    </div>
</template>
<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import {createPath} from "~/logic/Utils";
import Logo from "@/assets/logo.svg";

@Component
export default class Error500 extends Vue {
    private logo = Logo;
    getUrl(url:string):string{
        return createPath(url);
    }

}
</script>

<style scoped>
body, html {
    height: 100%;
}

.logo-for-error {
    height: 1em;
}

#homeBtn {
    margin-top: 10px;
    margin-bottom: 50px;
}
</style>