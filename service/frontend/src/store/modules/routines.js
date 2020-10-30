import Axios from 'axios';
import globals from '../../commons/globals';

export const routines = {
  namespaced: true,
  state: {
    routines: [],
    selectedRoutine: null
  },
  getters: {
    routines: state => state.routines
  },
  actions: {
    addRoutine() {},
    getRoutines({ commit }) {
      Axios.get('https://jrie.eu:8001/routines', {
        headers: {
          Accept: 'application/json'
        },
        auth: {
          username: globals.API_USERNAME,
          password: globals.API_PASSWORD
        }
      }).then(res => {
        commit('SET_ROUTINES', res.data.routines);
      });
    },

    setSelectedRoutine({ commit }, routine) {
      commit('SET_SELECTED_ROUTINE', routine);
    }
  },
  mutations: {
    SET_ROUTINES(state, routines) {
      state.routines = routines;
    },
    SET_SELECTED_ROUTINE(state, routine) {
      state.selectedRoutine = routine;
    }
  }
};
