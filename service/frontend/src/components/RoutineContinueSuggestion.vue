<template>
  <div>
    <v-row v-if="loading">
      <v-col cols="12">
        Loading suggestion ...
      </v-col>
      <v-col cols="12">
        <v-progress-linear indeterminate color="accent" />
      </v-col>
    </v-row>
    <v-row v-else-if="error">
      <ErrorHandler v-if="error" text="Sorry, could not load suggestions" />
    </v-row>
    <v-row v-else>
      <v-col>
        <h1>
          Suggested continuation of the routine:
        </h1>
      </v-col>
      <v-col v-for="(event, index) in suggestion" :key="index" cols="12">
        <RoutineContinueSuggestionItem
          :is-last-item="index === suggestion.length - 1"
          :event="event"
        />
      </v-col>
      <v-col cols="12">
        <v-btn
          color="accent"
          @click="applySuggestion(suggestion)"
          :disabled="!suggestion"
        >
          Apply suggestion
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import RoutineContinueSuggestionItem from '@/components/RoutineContinueSuggestionItem';
import { mapGetters, mapActions } from 'vuex';
import ErrorHandler from '@/components/ErrorHandler';
import { axiosInstance } from '@/config/axiosInstance';

export default {
  name: 'RoutineContinueSuggestion',
  components: { ErrorHandler, RoutineContinueSuggestionItem },

  data: () => ({
    suggestion: [],
    loading: true,
    error: false
  }),

  computed: {
    ...mapGetters('routines', ['selectedRoutine'])
  },

  mounted() {
    this.getSuggestion();
  },

  methods: {
    ...mapActions('routines', ['applySuggestion']),
    async getSuggestion() {
      let params = {
        deviceId: this.selectedRoutine.events[
          this.selectedRoutine.events.length - 1
        ].deviceId,
        eventId: this.selectedRoutine.events[
          this.selectedRoutine.events.length - 1
        ].eventId,
        limit: 4
      };
      await axiosInstance
        .get('suggestions/continuation', {
          params: params
        })
        .then(res => {
          this.suggestion = res.data.suggestions;
          console.log(res.data);
          this.loading = false;
        })
        .catch(e => {
          this.error = true;
          this.loading = false;
        });
    }
  }
};
</script>
