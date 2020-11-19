<template>
  <v-app>
    <Toolbar />
    <Drawer />
    <v-main>
      <router-view />
    </v-main>
    <Footer />
    <v-snackbar v-model="isSnackbarActive" :vertical="true">
      {{ snackbarMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="accent"
          text
          v-bind="attrs"
          @click="isSnackbarActive = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'App',
  components: {
    Toolbar: () => import('@/components/Toolbar.vue'),
    Drawer: () => import('@/components/Drawer.vue'),
    Footer: () => import('@/components/Footer.vue')
  },

  computed: {
    ...mapGetters('snackbar', ['snackbarMessage']),
    isSnackbarActive: {
      get() {
        return this.$store.state.snackbar.isSnackbarActive;
      },
      set(value) {
        this.setSnackbarActive(value);
      }
    }
  },

  methods: {
    ...mapActions('snackbar', ['setSnackbarActive']),
    ...mapActions('routines', ['handleAxios']),
    ...mapActions('house', ['getRooms', 'getDeviceTypes'])
  }
};
</script>
