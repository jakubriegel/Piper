export const routines = {
  namespaced: true,
  state: {
    routines: []
  },
  getters: {
    routines: state => state.routines
  },
  actions: {
    addEmptyRoutine({ commit }) {
      commit("ADD_EMPTY_ROUTINE");
    }
  },
  mutations: {
    ADD_EMPTY_ROUTINE(state) {
      state.routines.push({
        name: "",
        events: [
          {
            trigger: "",
            action: ""
          }
        ]
      });
    }
  }
};
