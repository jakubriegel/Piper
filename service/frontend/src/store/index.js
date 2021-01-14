import Vue from 'vue';
import Vuex from 'vuex';

import { navigation, routines, house } from './modules';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    navigation,
    routines,
    house
  }
});
