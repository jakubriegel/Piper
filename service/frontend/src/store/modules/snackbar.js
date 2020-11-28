export const snackbar = {
  namespaced: true,
  state: {
    isSnackbarActive: false,
    snackbarMessage: ''
  },

  getters: {
    snackbarMessage: state => state.snackbarMessage
  },

  actions: {
    setSnackbarMessage({ commit }, message) {
      commit('SET_SNACKBAR_MESSAGE', message);
    },
    setSnackbarActive({ commit }, value) {
      commit('SET_SNACKBAR_ACTIVE', value);
    }
  },
  mutations: {
    SET_SNACKBAR_MESSAGE(state, message) {
      state.snackbarMessage = message;
    },
    SET_SNACKBAR_ACTIVE(state, value) {
      state.isSnackbarActive = value;
    }
  }
};
