<template>
    <form @submit="searchForm">
        <div class="container h-75">
            <div class="row mt-4">
                <h4>
                    {{ doctorPagination.totalItems > 1 ? $t(resultsMessage, resultsMessageParam) : $t(resultsMessage) }}
                </h4>
            </div>
            <div class="row mt-4 justify-content-center">
                <input class="form-control w-100" type="text" name="name" v-model="inputName" :placeholder='$t("Name")'/>
            </div>
            <div class="row mt-4">
                <div class="col-4 px-3">
                    <div class="row mt-4">
                        <select class="select-css form-control w-100" type="text" name="specialties"
                                id="selEspecialidad" v-model="selectedSpecialty">
                            <option value="-1" disabled :selected="searchedSpecialties.length === 0">
                                {{ $t('Specialty') }}
                            </option>
                            <option :value="null">{{ $t('Any') }}</option>
                            <option v-for="specialty in specialties" :key="specialty.id"
                                    :value="specialty.id">
                                {{ specialty.name }}
                            </option>
                        </select>
                    </div>
                    <div class="row mt-4">
                        <select class="select-css form-control w-100" type="text" name="localities" id="localidad" v-model="selectedLocality">
                            <option value="-1" disabled :selected="searchedLocalities.length == 0">
                                {{ $t('Locality') }}
                            </option>
                            <option :value="null">{{ $t('Any') }}</option>
                            <option v-for="locality in localities" :key="locality.id" :value="locality.id">
                                {{ locality.name }}
                            </option>
                        </select>
                    </div>
                    <div class="row mt-4">
                        <button type="submit" class="btn btn-info w-100 rounded-pill">{{ $t('Filter') }}</button>
                    </div>
                </div>
                <div class="col-1"></div>
                <div class="col">
                    <div v-if="totalPages != 0" id="paging"
                         class="p-3 d-flex container w-100 justify-content-center ">
                        <div v-if="page>2">
                            <button type="button" class="btn btn-info btn-sm mr-1 firstButton" @click="first">{{ firstPage }}</button>
                        </div>
                        <div v-if="page > 1">
                            <button type="button" class="btn btn-info btn-sm prevButton" @click="previous">{{ prevPage }}</button>
                        </div>
                        <p class="d-inline mx-2">{{ $t('Page_of_totalPages', [page, totalPages]) }}</p>
                        <div v-if="remainingPages != 0">
                            <button type="button" class="btn btn-info btn-sm nextButton" @click="next">{{ nextPage }}</button>
                        </div>
                        <div v-if="remainingPages > 1">
                            <button type="button" class="btn btn-info btn-sm ml-1 lastButton" @click="last">{{ lastPage }}</button>
                        </div>
                    </div>
                    <ul class="list-group turno-list mr-2 w-100">
                        <div v-if="doctorPagination.items.length === 0" class="container-fluid justify-content-center">
                            <p class="text-center" style="color:grey;">{{ $t('NoMedicsFound') }}</p>
                        </div>
                        <li v-for="member in doctorPagination.items" :key="member.id" class="list-group-item turno-item mb-3">
                            <div class="container">
                                <div class="row">
                                    <div class="col-3 d-flex flex-column justify-content-center">
                                        <div class="profile-picture-container">
                                            <div style="margin-top: 100%;"></div>
                                            <img
                                                class="profile-picture rounded-circle"
                                                :src='member.user.hasPicture ? getApiUrl("users/" + member.user.id + "/picture") || defaultProfilePic : defaultProfilePic'
                                                alt="profile pic"
                                            />
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="row justify-content-start">
                                            <h5>{{ member.user.firstName + " " + member.user.surname }}</h5>
                                        </div>
                                        <div class="row">
                                            <p class="m-0">
                                                {{
                                                    member.specialtyIds.map((v) => {
                                                        return getSpecialtyName(v);
                                                    }).join(', ')
                                                }}
                                            </p>
                                        </div>
                                        <div class="row">
                                            <p class="m-0">
                                                {{
                                                    member.office.street + ' - ' + getLocalityName(member.office.localityId)
                                                }}</p>
                                        </div>
                                        <a
                                            class="link"
                                            :href='"https://www.google.com/maps/search/?api=1&query="+getLocalityName(member.office.localityId) + "," + member.office.street'
                                            target="_blank"
                                        >
                                            <div class="row">
                                                <small class="m-0">{{ $t('SeeInGoogleMaps') }}</small>
                                            </div>
                                        </a>
                                    </div>
                                    <div class="col d-flex justify-content-center align-items-center">
                                        <RouterLink :to="getUrl(`appointment/${member.id}/0`)">
                                            <button type="button" class="btn btn-info available-appointments-button"
                                                    :data-id="member.id">
                                                {{ $t('AvailableAppointments') }}
                                            </button>
                                        </RouterLink>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </ul>
                    <div v-if="totalPages != 0" id="paging"
                         class="p-3 d-flex container w-100 justify-content-center ">
                        <div v-if="page > 2">
                            <button type="button" class="btn btn-info btn-sm mr-1 firstButton" @click="first">{{ firstPage }}</button>
                        </div>
                        <div v-if="page > 1">
                            <button type="button" class="btn btn-info btn-sm prevButton" @click="previous">{{ prevPage }}</button>
                        </div>
                        <p class="d-inline mx-2">{{ $t('Page_of_totalPages', [page, totalPages]) }}</p>
                        <div v-if="remainingPages != 0">
                            <button type="button" class="btn btn-info btn-sm nextButton" @click="next">{{ nextPage }}</button>
                        </div>
                        <div v-if="remainingPages > 1">
                            <button type="button" class="btn btn-info btn-sm ml-1 lastButton" @click="last">{{ lastPage }}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</template>

<script lang="ts">
import {Component, Vue, Watch} from 'vue-property-decorator';
import {DoctorSpecialty} from '~/logic/models/DoctorSpecialty';
import {Locality} from '~/logic/models/Locality';
import {State} from 'vuex-class';
import {localityActionTypes} from '~/store/types/localities.types';
import {doctorSpecialtyActionTypes} from '~/store/types/doctorSpecialties.types';

import {createApiPath, createPath, ID, Nullable} from '~/logic/Utils';
import {DoctorService} from '~/logic/interfaces/services/DoctorService';
import TYPES from '~/logic/types';
import {Pagination} from '~/logic/models/utils/Pagination';
import {Doctor} from '~/logic/models/Doctor';
import {APIError} from '~/logic/models/APIError';
import defaultProfilePic from "@/assets/defaultProfilePic.svg";

type I18NMessages = 'NoResultsFound' | 'SearchResults1' | 'SearchResults2More';

@Component
export default class MedicList extends Vue {
    private static readonly PER_PAGE = 10;
    private readonly defaultProfilePic = defaultProfilePic;

    @State(state => state.localities.localities)
    private readonly localities: Locality[];
    @State(state => state.doctorSpecialties.doctorSpecialties)
    private readonly specialties: DoctorSpecialty[];
    private prevPage = '<';
    private firstPage = '<<';
    private nextPage = '>';
    private lastPage = '>>';
    private inputName: string = this.name;
    private selectedSpecialty: Nullable<ID> = null;
    private selectedLocality: Nullable<ID> = null;
    private resultsMessage: I18NMessages = 'NoResultsFound';
    private resultsMessageParam: number[] = [];
    private doctorPagination: Pagination<Doctor> = new Pagination<Doctor>([], 0);

    get page(): number {
        return parseInt(this.$route.params.page);
    }
    
    get totalPages(): number {
        return Math.ceil(this.doctorPagination.totalItems / MedicList.PER_PAGE);
    }

    get remainingPages(): number {
        return this.totalPages - this.page;
    }

    get searchedSpecialties(): DoctorSpecialty[] {
        let aux = this.$route.query.specialties;
        let searchedSpecialties: DoctorSpecialty[];

        if (typeof aux !== 'string' && typeof aux !== 'object') aux = [];
        if (typeof aux === 'string') aux = aux.split(',');

        searchedSpecialties = aux.map(v => {
            let filtered = this.specialties.filter(value => parseInt(v!) === value.id);
            return filtered.length > 0 ? filtered[0] : null;
        }).filter(value => value != null) as DoctorSpecialty[];

        if (this.selectedSpecialty && !searchedSpecialties.find(value => value.id == this.selectedSpecialty)) {
            let specialty = this.specialties.find(value => value.id == this.selectedSpecialty);
            if (specialty)
                searchedSpecialties.push(specialty);
        }

        return searchedSpecialties;
    }

    get searchedLocalities() {
        let aux = this.$route.query.localities;
        let searchedLocalities: Locality[];

        if (typeof aux !== 'string' && typeof aux !== 'object') aux = [];
        if (typeof aux === 'string') aux = aux.split(',');

        searchedLocalities = aux.map(v => {
            let filtered = this.localities.filter(value => parseInt(v!) === value.id);
            return filtered.length > 0 ? filtered[0] : null;
        }).filter(value => value != null) as Locality[];

        if (this.selectedLocality && !searchedLocalities.find(value => value.id == this.selectedLocality)) {
            let locality = this.localities.find(value => value.id == this.selectedLocality);
            if (locality)
                searchedLocalities.push(locality);
        }

        return searchedLocalities;
    }

    get name(): string {
        return typeof this.$route.query.name === 'string' ? this.$route.query.name : "";
    }

    getSpecialtyName(id: number): string {
        for (let specialty of this.specialties) {
            if (specialty.id === id) return specialty.name;
        }

        return id.toString();
    }

    getLocalityName(id: number): string {
        for (let locality of this.localities) {
            if (locality.id === id) return locality.name;
        }

        return id.toString();
    }

    getUrl(url:string):string{
        return createPath(url);
    }

    getApiUrl(url:string):string{
        return createApiPath(url);
    }

    searchForm(e: Event): void {
        e.stopPropagation();
        e.preventDefault();

        this.search();
    }

    async search() {
        this.$router.push({
            path: createPath("/mediclist/" + this.page.toString()),
            query: {
                name: this.name,
                specialties: this.selectedSpecialty?.toString(),
                localities: this.selectedLocality?.toString()
            }
        }).catch(() => {});

        let response = await this.getDoctorService().list({
            page: this.page,
            name: this.inputName,
            specialty: this.selectedSpecialty || undefined,
            locality: this.selectedLocality || undefined
        });
        if (!(response instanceof APIError)) {
            this.doctorPagination = response;
        }
    }

    first(): void {
        this.gotoPage(1);
    }

    next(): void {
        this.gotoPage(parseInt(this.$route.params.page) + 1);
    }

    previous(): void {
        let page = parseInt(this.$route.params.page);
        if (page > 1)
            this.gotoPage(page - 1);
    }

    last(): void {
        this.gotoPage(this.totalPages);
    }

    async mounted() {
        await this.$store.dispatch('localities/loadLocalities', localityActionTypes.loadLocalities());
        await this.$store.dispatch('doctorSpecialties/loadDoctorSpecialties', doctorSpecialtyActionTypes.loadDoctorSpecialties());

        this.selectedSpecialty = this.searchedSpecialties.length > 0 ? this.searchedSpecialties[0].id : null;
        this.selectedLocality = this.searchedLocalities.length > 0 ? this.searchedLocalities[0].id : null;

        await this.search();
    }

    @Watch('doctorPagination')
    private updateResultsMessage() {
        if (this.doctorPagination.items.length >= 2) {
            this.resultsMessage = 'SearchResults2More';
            this.resultsMessageParam = [this.doctorPagination.totalItems];
        } else if (this.doctorPagination.items.length === 1) {
            this.resultsMessage = 'SearchResults1';
        } else {
            this.resultsMessage = 'NoResultsFound';
        }
    }

    private getDoctorService(): DoctorService {
        return this.$container.get(TYPES.Services.DoctorService);
    }

    private gotoPage(page: number) {
        this.$router.push({
            path: createPath("/mediclist/" + page.toString()),
            query: this.$route.query
        }).catch(()=>{});
        this.search();
    }
}
</script>

<style scoped>
html, body {
    height: 100vh;
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
    background-color: rgb(214, 214, 214);
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