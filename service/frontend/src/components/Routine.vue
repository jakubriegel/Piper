<template>
  <v-row>
    <v-col cols="12" md="9">
      <v-btn fab dark @click="$router.push('/routines')">
        <v-icon> mdi-arrow-left </v-icon>
      </v-btn>
    </v-col>
    <v-col cols="12" md="3">
      <v-switch
        :label="
          selectedRoutine.enabled ? 'Routine enabled' : 'Routine disabled'
        "
        v-model="selectedRoutine.enabled"
      />
    </v-col>
    <v-col
      cols="12"
      v-if="selectedRoutine.events && !selectedRoutine.events.length"
    >
      <p class="red--text">
        Routine has no events!
      </p>
      <v-btn
        class="mt-2"
        dark
        :block="$vuetify.breakpoint.xs"
        @click="addEventToRoutine(0)"
      >
        Initialize events
      </v-btn>
    </v-col>
    <v-col cols="12" v-else>
      <v-text-field class="pt-0" label="Name" v-model="selectedRoutine.name" />
      <v-card>
        <Container
          @drop="onDrop"
          class="pa-3"
          drag-handle-selector=".column-drag-handle"
        >
          <Draggable
            v-for="(event, index) in selectedRoutine.events"
            :key="index"
          >
            <v-card class="draggable-item" outlined>
              <v-icon class="column-drag-handle"> mdi-view-headline </v-icon>
              <v-card-text>
                <v-text-field label="deviceId" v-model="event.deviceId" />
                <v-text-field label="eventId" v-model="event.eventId" />
              </v-card-text>
            </v-card>
            <v-hover v-slot="{ hover }">
              <div class="d-flex align-self-center justify-center flex-wrap">
                <v-icon
                  :class="{ 'on-hover': hover }"
                  @click="addEventToRoutine(index)"
                  size="40"
                >
                  mdi-arrow-down-thick
                </v-icon>
              </div>
            </v-hover>
          </Draggable>
        </Container>
      </v-card>
    </v-col>
    <v-col cols="12">
      <v-btn dark :block="$vuetify.breakpoint.xs" @click="saveChanges">
        Save changes
      </v-btn>
    </v-col>
  </v-row>
</template>

<script>
import { Container, Draggable } from 'vue-smooth-dnd';
import { mapActions, mapGetters } from 'vuex';
export default {
  name: 'Routine',

  components: { Container, Draggable },

  computed: {
    ...mapGetters('routines', ['selectedRoutine'])
  },

  methods: {
    ...mapActions('routines', [
      'addRoutine',
      'editRoutine',
      'addEventToRoutine',
      'setSelectedRoutineEvents'
    ]),
    saveChanges() {
      if (this.selectedRoutine.id) {
        this.editRoutine(this.selectedRoutine.id);
      } else {
        this.addRoutine();
      }
      this.$router.push('/routines');
    },
    onDrop(dropResult) {
      this.setSelectedRoutineEvents(
        this.applyDrag(this.selectedRoutine.events, dropResult)
      );
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
.column-drag-handle
  cursor: move
</style>
