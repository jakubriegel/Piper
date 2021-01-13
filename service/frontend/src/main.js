import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';
import Toasted from 'vue-toasted';

Vue.use(Toasted, {
  position: 'bottom-center',
  duration: 8000
});
Vue.config.productionTip = false;

export const vueInstance = new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app');
