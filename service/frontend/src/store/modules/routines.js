import Axios from 'axios';

export const routines = {
  namespaced: true,
  state: {
    routines: []
  },
  getters: {
    routines: state => state.routines
  },
  actions: {
    addRoutine() {},
    getRoutines({ commit }) {
      Axios.get('piper.jrie.eu/routines').then(res => {
        commit('SET_ROUTINES', res.data.routines);
      });
    }
  },
  mutations: {
    SET_ROUTINES(state, data) {
      state.routines = data;
    }
  }
};
