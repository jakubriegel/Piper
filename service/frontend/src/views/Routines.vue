<template>
  <v-row no-gutters>
    <v-col class="pa-3" cols="3" v-for="routine in routines" :key="routine.id">
      <Routine :sequence="routine" />
    </v-col>
    <v-col class="pa-3" cols="3">
      <v-hover v-slot="{ hover }">
        <v-card
          class="d-flex align-content-center justify-center flex-wrap"
          outlined
          height="100%"
          :elevation="hover ? 16 : 2"
          :class="{ 'on-hover': hover }"
          @click="addEmptyRoutine"
        >
          <v-icon size="50">mdi-plus-box</v-icon>
        </v-card>
      </v-hover>
    </v-col>
  </v-row>
</template>

<script>
import Routine from "@/components/Routine";
import { mapGetters, mapActions } from "vuex";
export default {
  name: "CustomRoutines",
  components: { Routine },
  computed: {
    ...mapGetters("routines", ["routines"])
  },
  methods: {
    ...mapActions("routines", ["addEmptyRoutine"])
  }
};
</script>

<style lang="sass" scoped>
.v-card.on-hover
  background-color: rgba(#FFF, 0.8)
  >.v-icon
    color: #2196f3
</style>
