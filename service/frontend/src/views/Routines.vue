<template>
  <v-container>
    <v-card>
      <v-data-table :headers="headers" :items="routines">
        <template v-slot:item.enabled="{ item }">
          <v-checkbox v-model="item.enabled"></v-checkbox>
        </template>
        <template v-slot:item.actions="{ item }">
          <v-icon
            small
            class="mr-2"
            @click="$router.push('/routine/' + item.id)"
          >
            mdi-pencil
          </v-icon>
          <v-icon small @click="deleteItem(item.id)">
            mdi-delete
          </v-icon>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
export default {
  name: "CustomRoutines",
  computed: {
    ...mapGetters("routines", ["routines"])
  },

  mounted() {
    this.getRoutines();
  },

  data: () => ({
    headers: [
      {
        text: "Id",
        align: "start",
        sortable: false,
        value: "id"
      },
      { text: "Name", value: "name" },
      { text: "Enabled", value: "enabled" },
      { text: "Actions", value: "actions" }
    ]
  }),

  methods: {
    ...mapActions('routines', ['getRoutines']),

    deleteItem(item) {
      console.log("Delete " + item);
    }
  }
};
</script>

<style lang="sass" scoped></style>
