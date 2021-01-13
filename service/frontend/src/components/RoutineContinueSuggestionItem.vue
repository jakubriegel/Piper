<template>
  <div>
    <v-card>
      <v-card-text>
        <p>
          <strong>Room: </strong>
          {{ roomName(event.roomId) }}
        </p>
        <p>
          <strong>Device: </strong>
          {{ getDeviceName(event.roomId, event.deviceId) }}
        </p>
        <p>
          <strong>Event: </strong>
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
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'RoutineContinueSuggestionItem',

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
