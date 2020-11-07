<template>
  <v-container>
    <v-row v-if="loading">
      <v-progress-circular indeterminate color="accent" />
    </v-row>
    <v-row v-else>
      <v-col cols="12">
        <Routine />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Routine from '@/components/Routine';
export default {
  name: 'EditRoutine',

  components: { Routine },

  props: {
    id: {
      type: String,
      required: true
    }
  },

  data: () => ({
    loading: true
  }),

  mounted() {
    this.getRoutine(this.id).then(() => {
      this.loading = false;
    });
  }
};
</script>

<style scoped></style>
