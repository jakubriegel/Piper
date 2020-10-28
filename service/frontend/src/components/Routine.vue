<template>
  <div>
    <Container @drop="onDrop">
      <Draggable v-for="(event, id) in sequence.events" :key="id">
        <v-card class="draggable-item" outlined>
          <v-card-text>
            <v-text-field label="deviceId" v-model="event.deviceId">
            </v-text-field>
            <v-text-field label="eventId" v-model="event.eventId">
            </v-text-field>
          </v-card-text>
        </v-card>
        <v-hover v-slot="{ hover }">
          <div class="d-flex align-self-center justify-center flex-wrap">
            <v-icon
              :class="{ 'on-hover': hover }"
              @click="addEvent(id)"
              size="40"
            >
              mdi-arrow-down-thick
            </v-icon>
          </div>
        </v-hover>
      </Draggable>
    </Container>
  </div>
</template>

<script>
import { Container, Draggable } from 'vue-smooth-dnd';
export default {
  name: 'Routine',
  components: { Container, Draggable },
  props: {
    sequence: {
      required: true
    }
  },
  methods: {
    addEvent(id) {
      let tail = [...this.sequence.events];
      let head = tail.splice(0, id + 1);
      this.sequence.events = [
        ...head,
        {
          trigger: '',
          action: ''
        },
        ...tail
      ];
    },
    onDrop(dropResult) {
      this.sequence.events = this.applyDrag(this.sequence.events, dropResult);
    },
    applyDrag(arr, dragResult) {
      const { removedIndex, addedIndex, payload } = dragResult;
      if (removedIndex === null && addedIndex === null) return arr;

      const result = [...arr];
      let itemToAdd = payload;

      if (removedIndex !== null) {
        itemToAdd = result.splice(removedIndex, 1)[0];
      }

      if (addedIndex !== null) {
        result.splice(addedIndex, 0, itemToAdd);
      }

      return result;
    }
  }
};
</script>

<style lang="sass" scoped>
.v-icon.on-hover
  color: #2196f3
</style>
