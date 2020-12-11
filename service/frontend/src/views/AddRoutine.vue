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

  data: () => ({
    loading: true,
    emptyRoutine: {
      name: '',
      enabled: false,
      events: [
        {
          deviceId: '',
          eventId: ''
        }
      ],
      configuration: {
        days: [],
        start: null,
        end: null
      }
    }
  }),

  mounted() {
    this.setSelectedRoutine(this.emptyRoutine).then(
      () => (this.loading = false)
    );
  },

  methods: {
    ...mapActions('routines', ['setSelectedRoutine'])
  }
};
</script>

<style scoped></style>
