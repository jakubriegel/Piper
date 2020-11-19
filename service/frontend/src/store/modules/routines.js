import Axios from 'axios';
import utils from '../../commons/utils';

export const routines = {
  namespaced: true,
  state: {
    routines: [],
    selectedRoutine: {}
  },
  getters: {
    routines: state => state.routines,
    selectedRoutine: state => state.selectedRoutine
  },
  actions: {
    handleAxios({ dispatch }, message) {
      dispatch('snackbar/setSnackbarActive', true, { root: true });
      dispatch('snackbar/setSnackbarMessage', message, { root: true });
    },

    addRoutine({ dispatch, state }) {
      Axios.post(
        utils.apiUrl + 'routines/',
        {
          name: state.selectedRoutine.name,
          enabled: state.selectedRoutine.enabled,
          events: state.selectedRoutine.events,
          configuration: state.selectedRoutine.configuration
        },
        {
          headers: {
            Accept: 'application/json'
          },
          auth: utils.authentication
        }
      )
        .then(res => {
          console.log(res);
        })
        .catch(e => {
          dispatch('handleAxios', e);
        });
    },
    getRoutines({ dispatch, commit }) {
      Axios.get(utils.apiUrl + 'routines', {
        headers: {
          Accept: 'application/json'
        },
        auth: utils.authentication
      })
        .then(res => {
          commit('SET_ROUTINES', res.data.routines);
        })
        .catch(e => {
          dispatch('handleAxios', e);
        });
    },

    getRoutine({ dispatch, commit }, id) {
      Axios.get(utils.apiUrl + 'routines/' + id, {
        headers: {
          Accept: 'application/json'
        },
        auth: utils.authentication
      })
        .then(res => {
          commit('SET_SELECTED_ROUTINE', res.data.routine);
        })
        .catch(e => {
          dispatch('handleAxios', e);
        });
    },

    editRoutine({ dispatch, state }, id) {
      Axios.put(
        utils.apiUrl + 'routines/' + id,
        {
          name: state.selectedRoutine.name,
          enabled: state.selectedRoutine.enabled,
          events: state.selectedRoutine.events,
          configuration: state.selectedRoutine.configuration
        },
        {
          headers: {
            Accept: 'application/json'
          },
          auth: utils.authentication
        }
      )
        .then(res => {
          console.log(res);
        })
        .catch(e => {
          dispatch('handleAxios', e);
        });
    },

    addEventToRoutine({ commit, state }, index) {
      if (!state.selectedRoutine.events) {
        commit('ASSIGN_EVENTS_TO_ROUTINE', [
          {
            deviceId: '',
            eventId: ''
          }
        ]);
      } else {
        let tail = [...state.selectedRoutine.events];
        let head = tail.splice(0, index + 1);

        commit('ASSIGN_EVENTS_TO_ROUTINE', [
          ...head,
          {
            deviceId: '',
            eventId: ''
          },
          ...tail
        ]);
      }
    },

    setSelectedRoutine({ commit }, routine) {
      commit('SET_SELECTED_ROUTINE', routine);
    },

    setSelectedRoutineEvents({ commit }, events) {
      commit('ASSIGN_EVENTS_TO_ROUTINE', events);
    }
  },
  mutations: {
    SET_ROUTINES(state, routines) {
      state.routines = routines;
    },
    SET_SELECTED_ROUTINE(state, routine) {
      state.selectedRoutine = routine;
    },
    ASSIGN_EVENTS_TO_ROUTINE(state, events) {
      state.selectedRoutine.events = [...events];
    }
  }
};
