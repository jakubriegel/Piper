<template>
  <v-container>
    <v-row v-if="loading">
      <v-progress-linear indeterminate color="accent" />
    </v-row>
    <div v-else-if="$vuetify.breakpoint.smAndDown">
      <v-col
        v-for="(event, index) in loadedRoutine.events"
        :key="index"
        md="auto"
        class="pt-0 pb-0"
      >
        <RoutineExpansionPanelItem
          :is-last-item="index === loadedRoutine.events.length - 1"
          :event="event"
        />
      </v-col>
    </div>
    <v-row v-else>
      <v-col
        v-for="(event, index) in loadedRoutine.events"
        :key="index"
        md="auto"
      >
        <RoutineExpansionPanelItem
          :is-last-item="index === loadedRoutine.events.length - 1"
          :event="event"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import RoutineExpansionPanelItem from '@/components/RoutineExpansionPanelItem';

export default {
  name: 'RoutineExpansionPanel',
  components: { RoutineExpansionPanelItem },
  props: {
    loading: {
      required: true,
      type: Boolean
    }
  },

  watch: {
    routine(newRoutine, oldRoutine) {
      this.loadedRoutine = newRoutine;
      this.$emit('loadingComplete');
    }
  },

  computed: {
    ...mapGetters('routines', ['selectedRoutine']),

    routine() {
      return this.selectedRoutine;
    }
  },

  data: () => ({
    loadedRoutine: {}
  }),

  methods: {
    ...mapActions('routines', ['getRoutine'])
  }
};
</script>
