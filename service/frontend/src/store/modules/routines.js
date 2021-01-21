import { axiosInstance } from '@/config/axiosInstance';

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
    addRoutine({ state }) {
      axiosInstance
        .post('routines/', {
          name: state.selectedRoutine.name,
          enabled: state.selectedRoutine.enabled,
          events: state.selectedRoutine.events,
          configuration: state.selectedRoutine.configuration
        })
        .then(res => {
          console.log(res);
        });
    },
    getRoutines({ commit }) {
      axiosInstance.get('routines').then(res => {
        commit('SET_ROUTINES', res.data.routines);
      });
    },

    async getRoutine({ dispatch, commit }, id) {
      await axiosInstance.get('routines/' + id).then(res => {
        commit('SET_SELECTED_ROUTINE', res.data.routine);
      });
    },

    editRoutine({ state }, id) {
      axiosInstance
        .put('routines/' + id, {
          name: state.selectedRoutine.name,
          enabled: state.selectedRoutine.enabled,
          events: state.selectedRoutine.events,
          configuration: state.selectedRoutine.configuration
        })
        .then(res => {
          console.log(res);
        });
    },

    async deleteRoutine({ state }, id) {
      await axiosInstance.delete('routines/' + id).then(res => {
        console.log(res);
      });
    },

    addEventToRoutine({ commit, state }, index) {
      if (!state.selectedRoutine.events) {
        commit('ASSIGN_EVENTS_TO_ROUTINE', [
          {
            roomId: '',
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

    applySuggestion({ commit }, suggestion) {
      commit('APPLY_SUGGESTION', suggestion);
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
    APPLY_SUGGESTION(state, suggestion) {
      state.selectedRoutine.events = [
        ...state.selectedRoutine.events,
        ...suggestion
      ];
    },
    SET_SELECTED_ROUTINE(state, routine) {
      state.selectedRoutine = routine;
    },
    ASSIGN_EVENTS_TO_ROUTINE(state, events) {
      state.selectedRoutine.events = [...events];
    }
  }
};
