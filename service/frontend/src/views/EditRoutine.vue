<template>
  <v-container>
    <v-row v-if="loading">
      <v-progress-circular indeterminate color="accent" />
    </v-row>
    <v-row v-else>
      <v-col cols="12" sm="6">
        <Routine />
      </v-col>
      <v-col cols="12" sm="6">
        <RoutineContinueSuggestion />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import RoutineContinueSuggestion from '@/components/RoutineContinueSuggestion';
import Routine from '@/components/Routine';
import { mapActions, mapGetters } from 'vuex';
export default {
  name: 'EditRoutine',

  components: { RoutineContinueSuggestion, Routine },

  props: {
    id: {
      type: String,
      required: true
    }
  },

  computed: {
    ...mapGetters('routines', ['selectedRoutine'])
  },

  data: () => ({
    loading: true
  }),

  mounted() {
    this.getRoutine(this.id).then(() => {
      this.loading = false;
    });
  },

  methods: {
    ...mapActions('routines', ['getRoutine'])
  }
};
</script>

<style scoped></style>
