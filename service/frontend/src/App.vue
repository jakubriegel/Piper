<template>
  <v-app>
    <Toolbar />
    <Drawer />
    <v-main>
      <router-view />
    </v-main>
    <Footer />
  </v-app>
</template>

<script>
export default {
  name: 'App',

  components: {
    Toolbar: () => import('@/components/Toolbar.vue'),
    Drawer: () => import('@/components/Drawer.vue'),
    Footer: () => import('@/components/Footer.vue')
  }
};
</script>
