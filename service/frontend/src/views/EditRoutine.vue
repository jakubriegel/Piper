<template>
  <v-container>
    <v-row v-if="loading">
      <v-progress-circular indeterminate color="accent" />
    </v-row>
    <v-row v-else-if="error">
      <ErrorHandler text="Sorry, could not load routine" />
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
import ErrorHandler from '@/components/ErrorHandler';
export default {
  name: 'EditRoutine',

  components: { ErrorHandler, RoutineContinueSuggestion, Routine },

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
    loading: true,
    error: false
  }),

  mounted() {
    this.getRoutine(this.id)
      .then(() => {
        this.loading = false;
      })
      .catch(e => {
        this.error = true;
        this.loading = false;
      });
  },

  methods: {
    ...mapActions('routines', ['getRoutine'])
  }
};
</script>
