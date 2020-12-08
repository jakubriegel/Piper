<template>
  <v-container>
    <v-text-field
      clearable
      outlined
      v-model="search"
      label="Search"
    ></v-text-field>
    <v-card>
      <v-data-table
        :search="search"
        :headers="headers"
        :items="routines"
        :single-expand="true"
        :expanded="expanded"
        item-key="id"
        show-expand
        class="elevation-1"
        @item-expanded="loadExpandedRoutine"
      >
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
        <template v-slot:expanded-item="{ headers }">
          <td :colspan="headers.length">
            <RoutineExpansionPanel
              :loading="loading"
              @loadingComplete="setExpandableLoading(false)"
            />
          </td>
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
import RoutineExpansionPanel from '@/components/RoutineExpansionPanel';
export default {
  name: 'Routines',
  components: { RoutineExpansionPanel },
  computed: {
    ...mapGetters('routines', ['routines'])
  },

  mounted() {
    this.getRoutines();
  },

  data: () => ({
    search: '',
    loading: true,
    expanded: [],
    headers: [
      {
        text: 'Id',
        align: 'start',
        sortable: false,
        value: 'id'
      },
      { text: 'Name', value: 'name' },
      { text: 'Enabled', value: 'enabled' },
      { text: 'Actions', value: 'actions' },
      { text: '', value: 'data-table-expand' }
    ]
  }),

  methods: {
    ...mapActions('routines', ['getRoutine', 'getRoutines', 'deleteRoutine']),

    deleteItem(id) {
      this.deleteRoutine(id);
    },

    loadExpandedRoutine({ item, value }) {
      if (value) {
        this.getRoutine(item.id);
        this.setExpandableLoading(true);
      }
    },

    setExpandableLoading(value) {
      this.loading = value;
    }
  }
};
</script>

<style lang="sass" scoped></style>
