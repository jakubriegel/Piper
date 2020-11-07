<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <Routine :id="id" />
      </v-col>
    </v-row>
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
  }
};
</script>

<style scoped></style>
