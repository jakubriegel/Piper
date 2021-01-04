<template>
  <v-alert color="error" icon="mdi-alert">
    {{ text }}
  </v-alert>
</template>

<script>
export default {
  name: 'ErrorHandler',
  props: {
    text: {
      type: String,
      required: true
    }
  }
};
</script>

<style scoped></style>
