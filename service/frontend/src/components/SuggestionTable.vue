<template>
  <v-card>
    <v-card-title>
      Suggested routines
    </v-card-title>
    <v-row>
      <v-col v-if="loading" cols="12">
        <v-progress-linear></v-progress-linear>
      </v-col>
      <v-card-text v-else>
        <div v-for="(events, index) in suggestions" :key="index">
          <v-divider />
          <div class="d-md-flex justify-center align-center">
            <SuggestionExpansionPanel :events="events" />
            <v-btn
              dark
              class="mr-3 mb-3 mb-md-0"
              @click="
                $router.push({
                  name: 'Add Routine',
                  params: { routineToAdd: wrapEvents(events) }
                })
              "
            >
              Add this routine
            </v-btn>
          </div>
        </div>
      </v-card-text>
    </v-row>
  </v-card>
</template>

<script>
import Axios from 'axios';
import utils from '@/commons/utils';
import SuggestionExpansionPanel from '@/components/SuggestionExpansionPanel';

export default {
  name: 'SuggestionTable',
  components: { SuggestionExpansionPanel },
  data: () => ({
    suggestions: [],
    loading: true,
    headers: [
      { text: 'Suggested event chain', sortable: false, value: 'events' },
      { text: '', sortable: false, value: 'add' }
    ]
  }),

  mounted() {
    this.getSuggestions();
  },

  methods: {
    getSuggestions() {
      Axios.get(utils.apiUrl + 'suggestions/routines', {
        headers: {
          Accept: 'application/json'
        },
        auth: utils.authentication
      }).then(res => {
        this.suggestions = res.data.suggestions;
        this.loading = false;
      });
    },

    wrapEvents(events) {
      return { name: 'suggestion', enabled: false, events: events };
    }
  }
};
</script>

<style scoped></style>
