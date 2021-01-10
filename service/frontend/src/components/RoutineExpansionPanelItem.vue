<template>
  <div v-if="$vuetify.breakpoint.smAndDown">
    <v-card class="pa-4">
      <v-card-text>
        <p class="mb-0">
          {{ roomName(event.roomId) }}
        </p>
        <p class="mb-0">
          {{ getDeviceName(event.roomId, event.deviceId) }}
        </p>
        <p class="mb-0">
          {{ getEventName(event.deviceId, event.eventId) }}
        </p>
      </v-card-text>
    </v-card>
    <v-flex v-if="!isLastItem" class="d-flex justify-center">
      <v-icon>
        mdi-arrow-down
      </v-icon>
    </v-flex>
  </div>
  <v-row v-else>
    <v-card class="pa-4">
      <v-card-text>
        <p class="mb-0">
          {{ roomName(event.roomId) }}
        </p>
        <p class="mb-0">
          {{ getDeviceName(event.roomId, event.deviceId) }}
        </p>
        <p class="mb-0">
          {{ getEventName(event.deviceId, event.eventId) }}
        </p>
      </v-card-text>
    </v-card>
    <v-flex v-if="!isLastItem" class="d-flex">
      <v-icon>
        mdi-arrow-right
      </v-icon>
    </v-flex>
  </v-row>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'RoutineExpansionPanelItem',

  props: {
    isLastItem: {
      default: false,
      type: Boolean
    },

    event: {
      required: true,
      type: Object
    }
  },

  computed: {
    ...mapGetters('house', [
      'roomName',
      'devicesInRoom',
      'eventsForDeviceType',
      'deviceTypeByDeviceId'
    ])
  },

  methods: {
    getDeviceName(roomId, deviceId) {
      return this.devicesInRoom(roomId).filter(e => e.id === deviceId)[0].name;
    },

    getEventName(deviceId, eventId) {
      return this.eventsForDeviceType(
        this.deviceTypeByDeviceId(deviceId)
      ).filter(e => e.id === eventId)[0].name;
    }
  }
};
</script>
