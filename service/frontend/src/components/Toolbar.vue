<template>
  <v-app-bar app dark color="primary">
    <v-app-bar-nav-icon class="hidden-md-and-up" @click="toggleDrawer" />
    <v-toolbar-title class="headline text-uppercase">
      <span class="accent--text">PIPER</span>
      <span class="font-weight-light"> - </span>
      <span class="font-weight-light">HOME AUTOMATION</span>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-toolbar-items class="hidden-sm-and-down">
      <v-btn
        v-for="(route, i) in routes"
        :key="i"
        :to="route.to"
        class="ml-0 hidden-sm-and-down"
      >
        {{ route.text }}
      </v-btn>
    </v-toolbar-items>
  </v-app-bar>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';

export default {
  computed: {
    ...mapGetters('navigation', ['routes'])
  },
  methods: {
    ...mapMutations('navigation', ['toggleDrawer'])
  }
};
</script>
