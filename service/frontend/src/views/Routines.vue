<template>
  <v-container>
    <v-text-field
      clearable
      outlined
      v-model="search"
      label="Search"
    ></v-text-field>
    <v-card>
      <v-data-table :search="search" :headers="headers" :items="routines">
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
    <v-btn dark class="mt-3" @click="$router.push('/routine')">
      Add routine
    </v-btn>
  </v-container>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
export default {
  name: 'Routines',
  computed: {
    ...mapGetters('routines', ['routines'])
  },

  mounted() {
    this.getRoutines();
  },

  data: () => ({
    search: '',
    headers: [
      {
        text: 'Id',
        align: 'start',
        sortable: false,
        value: 'id'
      },
      { text: 'Name', value: 'name' },
      { text: 'Enabled', value: 'enabled' },
      { text: 'Actions', value: 'actions' }
    ]
  }),

  methods: {
    ...mapActions('routines', ['getRoutines']),

    deleteItem(item) {
      console.log('Delete ' + item);
    }
  }
};
</script>

<style lang="sass" scoped></style>
