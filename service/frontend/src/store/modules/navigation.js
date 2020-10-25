export const navigation = {
  namespaced: true,
  state: {
    drawer: false,
    routes: [
      {
        text: "Home",
        to: "/"
      },
      {
        text: "Routines",
        to: "/routines"
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
};
