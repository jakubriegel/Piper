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
import { mapActions } from 'vuex';
export default {
  name: 'AddRoutine',

  components: { Routine },

  props: {
    routineToAdd: {
      required: false,
      default: () => {
        return {
          name: '',
          enabled: false,
          events: [
            {
              roomId: '',
              deviceId: '',
              eventId: ''
            }
          ],
          configuration: {
            days: [],
            start: null,
            end: null
          }
        };
      }
    }
  },

  data: () => ({
    loading: true
  }),

  mounted() {
    this.setSelectedRoutine(this.routineToAdd).then(
      () => (this.loading = false)
    );
  },

  methods: {
    ...mapActions('routines', ['setSelectedRoutine'])
  }
};
</script>
