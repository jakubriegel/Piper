<template>
  <div>
    <v-row class="hidden-md-and-up justify-center">
      <v-btn @click="saveChanges">Save changes</v-btn>
    </v-row>
    <v-row>
      <v-col cols="6" class="mr-5">
        <v-text-field label="Name" v-model="routine.name"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-checkbox label="Enabled" v-model="routine.enabled"></v-checkbox>
      </v-col>
      <v-col cols="2" class="hidden-sm-and-down">
        <v-btn @click="saveChanges"> Save changes</v-btn>
      </v-col>
    </v-row>
    <div v-if="loading">
      <v-progress-circular indeterminate color="accent" />
    </div>
    <div v-else-if="routine && !routine.events.length">
      <v-btn @click="addFirstEvent()">Add event</v-btn>
    </div>
    <v-card v-else>
      <Container @drop="onDrop">
        <Draggable v-for="(event, index) in routine.events" :key="index">
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
    </v-card>
  </div>
</template>

<script>
import { Container, Draggable } from 'vue-smooth-dnd';
import Axios from 'axios';
import globals from '@/commons/globals';
export default {
  name: 'Routine',

  components: { Container, Draggable },

  props: {
    id: {
      required: true
    }
  },

  data() {
    return {
      routine: {},
      loading: true
    };
  },

  mounted() {
    Axios.get('https://jrie.eu:8001/routines/' + this.id, {
      headers: {
        Accept: 'application/json'
      },
      auth: {
        username: globals.API_USERNAME,
        password: globals.API_PASSWORD
      }
    })
      .then(res => {
        this.routine = res.data.routine;
      })
      .catch(() => {
        //TODO handle error
      })
      .finally(() => {
        this.loading = false;
      });
  },

  methods: {
    saveChanges() {
      Axios.put('https://jrie.eu:8001/routines/' + this.id, this.routine, {
        headers: {
          Accept: 'application/json'
        },
        auth: {
          username: globals.API_USERNAME,
          password: globals.API_PASSWORD
        }
      })
        .then(res => {
          console.log(res);
        })
        .catch(() => {
          //TODO handle error
        });
    },
    addFirstEvent() {
      this.routine.events.push({
        trigger: '',
        action: ''
      });
      console.log(this.routine);
    },
    addEvent(id) {
      let tail = [...this.routine.events];
      let head = tail.splice(0, id + 1);
      this.routine.events = [
        ...head,
        {
          trigger: '',
          action: ''
        },
        ...tail
      ];
    },
    onDrop(dropResult) {
      this.routine.events = this.applyDrag(this.routine.events, dropResult);
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
