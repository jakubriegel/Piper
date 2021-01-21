<template>
  <v-app>
    <Toolbar />
    <Drawer />
    <v-main>
      <router-view />
    </v-main>
    <v-parallax src="tech.jpg" height="150" class="white--text text-center">
      <h2>Make your house smarter!</h2>
    </v-parallax>
    <Footer />
  </v-app>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'App',

  components: {
    Toolbar: () => import('@/components/Toolbar.vue'),
    Drawer: () => import('@/components/Drawer.vue'),
    Footer: () => import('@/components/Footer.vue')
  },

  mounted() {
    this.getRooms();
    this.getDeviceTypes();
  },

  methods: {
    ...mapActions('house', ['getRooms', 'getDeviceTypes'])
  }
};
</script>
