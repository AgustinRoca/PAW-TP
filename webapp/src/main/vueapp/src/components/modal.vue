<template>
    <b-modal 
        v-model="showModal" 
        :title="title" 
        @ok="onConfirm"
        @cancel="onCancel"
        :no-close-on-backdrop="true">
        <template slot="modal-header">
            <h5 class="modal-title">{{title}}</h5>
        </template>
        {{ body }}
        <template #modal-footer="{ok,cancel}">
            {{ footer }}
            <button @click="cancel()" type="button" class="btn btn-secondary">
                {{ cancelText }}
            </button>
            <button @click="ok()" type="button" class="btn btn-primary">
                {{ confirmText }}
            </button>
        </template>
    </b-modal>
</template>

<script lang="ts">
import {Component, Emit, Prop, VModel, Vue} from 'vue-property-decorator';
import i18n from "@/plugins/i18n";
import { BvModalEvent } from 'bootstrap-vue';

@Component
export default class Modal extends Vue {
    @Prop({type:String,default:""})
    private readonly title:string;
    @Prop({type:String,default:""})
    private readonly body:string;
    @Prop({type:String,default:""})
    private readonly footer:string;
    @Prop({type:String,default:i18n.t("Cancel")})
    private readonly cancelText:string;
    @Prop({type:String,default:i18n.t("Accept")})
    private readonly confirmText:string;

    @VModel({type:Boolean})
    private showModal:boolean;

    @Emit("cancel")
    onCancel():boolean{
        return false;
    }

    @Emit("confirm")
    onConfirm():boolean{
        return true;
    }
}
</script>