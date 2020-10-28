<template>
  <v-container>
    <Routine :sequence="routine" />
  </v-container>
</template>

<script>
import Routine from '@/components/Routine';
export default {
  name: 'EditRoutine',

  components: { Routine },

  props: {
    id: {
      type: String,
      required: true
    }
  },

  data: () => ({
    routine: null
  }),

  mounted() {
    //TODO axios get routine by id
  }
};
</script>

<style scoped></style>
