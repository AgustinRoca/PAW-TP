import Vue from 'vue'
import App from './App.vue'
import router from './plugins/router'
import i18n from './plugins/i18n'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import store from '~/plugins/vuex';
import {inversifyPlugin} from '~/plugins/inversify';
import VueSocialSharing from 'vue-social-sharing'

Vue.use(BootstrapVue);
Vue.use(IconsPlugin);
Vue.use(inversifyPlugin);
Vue.use(VueSocialSharing);


Vue.config.productionTip = false;

store.commit('auth/loadUserFromLocalStorage');

new Vue({
  router,
  i18n,
  store,
  render: h => h(App),
}).$mount('#app');

// code for generating the communication button
let options = {
  facebook: "110403960669937", // Facebook page ID
  whatsapp: "+54 9 11 6939-7444", // WhatsApp number
  call_to_action: i18n.t("HereToHelp"), // Call to action
  button_color: "#333", // Color of button
  position: "right", // Position may be 'right' or 'left'
  order: "whatsapp", // Order of buttons
};
let proto = document.location.protocol, host = "whatshelp.io", url = proto + "//static." + host;
let s = document.createElement('script');
s.type = 'text/javascript';
s.async = true;
s.src = url + '/widget-send-button/js/init.js';
s.onload = function () {
  //@ts-ignore
  WhWidgetSendButton.init(host, proto, options);
};
let x = document.getElementsByTagName('script')[0];
//@ts-ignore
x.parentNode.insertBefore(s, x);