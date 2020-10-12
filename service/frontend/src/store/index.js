import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    routes: [
      {
        text: "Dashboard",
        to: "/"
      },
      {
        text: "Search",
        to: "/search"
      },
      {
        text: "About",
        to: "/about"
      }
    ]
  },
  getters: {
    routes: state => state.routes
  },
  mutations: {
    setDrawer: (state, payload) => (state.drawer = payload),
    toggleDrawer: state => (state.drawer = !state.drawer)
  },
  actions: {},
  modules: {}
});
