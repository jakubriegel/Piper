import Vue from "vue";
import Vuex from "vuex";

import { drawer } from "./modules";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    drawer
  }
});
