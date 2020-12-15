<template>
  <v-container>
    <div v-if="$vuetify.breakpoint.smAndDown">
      <v-col
        v-for="(event, index) in events"
        :key="index"
        md="auto"
        class="pt-0 pb-0"
      >
        <RoutineExpansionPanelItem
          :is-last-item="index === events.length - 1"
          :event="event"
        />
      </v-col>
    </div>
    <v-row v-else>
      <v-col v-for="(event, index) in events" :key="index" md="auto">
        <RoutineExpansionPanelItem
          :is-last-item="index === events.length - 1"
          :event="event"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import RoutineExpansionPanelItem from '@/components/RoutineExpansionPanelItem';

export default {
  name: 'SuggestionExpansionPanel',
  components: { RoutineExpansionPanelItem },
  props: {
    events: {
      required: true,
      type: Array
    }
  }
};
</script>

<style scoped></style>
