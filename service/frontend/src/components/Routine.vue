<template>
  <div>
    <v-row class="hidden-md-and-up justify-center">
      <v-btn @click="saveChanges">Save changes</v-btn>
    </v-row>
    <v-row>
      <v-col cols="6" class="mr-5">
        <v-text-field
          label="Name"
          v-model="selectedRoutine.name"
        ></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-checkbox
          label="Enabled"
          v-model="selectedRoutine.enabled"
        ></v-checkbox>
      </v-col>
      <v-col cols="2" class="hidden-sm-and-down">
        <v-btn @click="saveChanges"> Save changes</v-btn>
      </v-col>
    </v-row>
    <div v-if="loading">
      <v-progress-circular indeterminate color="accent" />
    </div>
    <v-card v-else>
      <Container @drop="onDrop">
        <Draggable
          v-for="(event, index) in selectedRoutine.events"
          :key="index"
        >
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
  </div>
</template>

<script>
import { Container, Draggable } from 'vue-smooth-dnd';
import { mapActions, mapGetters } from 'vuex';
export default {
  name: 'Routine',

  components: { Container, Draggable },

  props: {
    id: {
      required: true
    }
  },

  computed: {
    ...mapGetters('routines', ['selectedRoutine'])
  },

  data: () => ({
    routine: {},
    loading: true
  }),

  mounted() {
    this.getRoutine(this.id).then(() => {
      this.loading = false;
    });
  },

  methods: {
    ...mapActions('routines', [
      'getRoutine',
      'editRoutine',
      'addEventToRoutine',
      'setSelectedRoutineEvents'
    ]),
    saveChanges() {
      this.editRoutine(this.id);
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
</style>
