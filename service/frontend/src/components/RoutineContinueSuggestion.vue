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
import Axios from 'axios';
import utils from '@/commons/utils';
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'RoutineContinueSuggestion',
  components: { RoutineContinueSuggestionItem },

  data: () => ({
    suggestion: [],
    loading: true
  }),

  computed: {
    ...mapGetters('routines', ['selectedRoutine'])
  },

  created() {
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
      await Axios.get(utils.apiUrl + 'suggestions/continuation', {
        params: params,
        headers: {
          Accept: 'application/json'
        },
        auth: utils.authentication
      }).then(res => {
        this.suggestion = res.data.suggestions;
        console.log(res.data);
        this.loading = false;
      });
    }
  }
};
</script>

<style scoped></style>
