<template>
    <div class="container w-100 justify-content-end">
        <RouterLink v-if="user.verified" :to="getUrl('/patient/home')" class="header-a-element nav-link mx-3">
            {{ $t('MyAppointments') }}
        </RouterLink>
        <RouterLink v-if="!isDoctor" :to='getUrl("mediclist/1")'
                     class="header-a-element nav-link mx-3">{{ $t('SearchMedics') }}
        </RouterLink>

        <div class="d-inline-flex flex-column align-items-end mr-3">
            <RouterLink v-if="!isDoctor" :to="getUrl('patient/profile')">
                <p style="font-weight: 400;" class="m-0 p-0 text-muted white-text">
                    {{ user.firstName + ' ' + user.surname }}
                </p>
            </RouterLink>
            <RouterLink v-else :to="getUrl('doctor/profile')">
                <p style="font-weight: 400;" class="m-0 p-0 text-muted white-text">
                    {{ user.firstName + ' ' + user.surname }}
                </p>
            </RouterLink>
            <a @click="logout" href="" class="m-0 p-0 header-a-element"><small
                class="m-0 p-0">{{ $t('Logout') }}</small></a>
        </div>
        <img v-if="!user.verified" id="navbarUnverifiedUserImage" class="ml-2"
             src="https://fonts.gstatic.com/s/i/materialicons/account_circle/v4/24px.svg" alt="unverified user image">
        <RouterLink v-else-if="!isDoctor" :to='getUrl("patient/profile")'>
            <div style="width: 2em;height:2em;" class="d-flex flex-column justify-content-center">
                <div class="profile-picture-container">
                    <img
                        id="navbarPatientUserImage"
                        class="profile-picture rounded-circle"
                        :src="profilePicUrl"
                        alt="profile pic"
                    />
                </div>
            </div>
        </RouterLink>
        <RouterLink v-else :to='getUrl("doctor/profile")'>
            <div style="width: 2em;height:2em;" class="d-flex flex-column justify-content-center">
                <div class="profile-picture-container">
                    <img
                        id="navbarDoctorUserImage"
                        class="profile-picture rounded-circle"
                        :src="profilePicUrl"
                        alt="profile pic"
                    />
                </div>
            </div>
        </RouterLink>
    </div>
</template>

<script lang="ts">
import {Component, Emit, Vue} from 'vue-property-decorator';
import {State} from 'vuex-class';
import {User} from '~/logic/models/User';
import defaultProfilePic from "@/assets/defaultProfilePic.svg";

import {createApiPath, createPath, Nullable} from '~/logic/Utils';

@Component
export default class NavbarLogged extends Vue {
    @State(state => state.users.profilePictureTimestamp)
    private timestamp: Nullable<number>;
    @State(state => state.auth.user)
    private readonly user: User;
    @State(state => state.auth.isDoctor)
    private readonly isDoctor: boolean;
    private readonly defaultProfilePic = defaultProfilePic;

    get profilePicUrl(): string {
        let ts;
        if (this.timestamp)
            ts = `?ts=${this.timestamp}`;
        else
            ts = '';

        return this.getApiUrl(`/users/${this.user.id}/picture${ts}`) || defaultProfilePic;
    }

    getApiUrl(url: string):string{
        return createApiPath(url);
    }

    @Emit("logout")
    logout(e: Event): void {
        e.preventDefault();
    };
    getUrl(url:string):string{
        return createPath(url);
    }
}
</script>