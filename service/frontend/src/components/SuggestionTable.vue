<template>
  <v-card>
    <v-card-title>
      Suggested routines
    </v-card-title>
    <v-row>
      <v-col v-if="loading" cols="12">
        <v-progress-linear></v-progress-linear>
      </v-col>
      <v-card-text v-else-if="houseLoaded">
        <ErrorHandler v-if="error" text="Sorry, could not load suggestions" />
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
import SuggestionExpansionPanel from '@/components/SuggestionExpansionPanel';
import ErrorHandler from '@/components/ErrorHandler';
import { axiosInstance } from '@/config/axiosInstance';

export default {
  name: 'SuggestionTable',

  components: { SuggestionExpansionPanel, ErrorHandler },

  props: {
    houseLoaded: {
      type: Boolean,
      required: true
    }
  },

  data: () => ({
    suggestions: [],
    loading: true,
    error: false,
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
      axiosInstance
        .get('suggestions/routines')
        .then(res => {
          this.suggestions = res.data.suggestions;
          this.loading = false;
        })
        .catch(e => {
          this.error = true;
          this.loading = false;
        });
    },

    wrapEvents(events) {
      return { name: 'suggestion', enabled: false, events: events };
    }
  }
};
</script>
