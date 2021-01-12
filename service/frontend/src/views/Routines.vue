<template>
  <v-container>
    <v-row>
      <v-col cols="12">
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
              <v-switch
                v-model="item.enabled"
                @change="enableRoutine(item.id, item.enabled)"
              ></v-switch>
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
          Add new routine
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <SuggestionTable />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import RoutineExpansionPanel from '@/components/RoutineExpansionPanel';
import SuggestionTable from '@/components/SuggestionTable';
import Axios from 'axios';
import utils from '@/commons/utils';
export default {
  name: 'Routines',
  components: { SuggestionTable, RoutineExpansionPanel },
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
    },

    enableRoutine(id, enabled) {
      if (enabled) {
        Axios.put(
          utils.apiUrl + 'routines/' + id + '/enable',
          {},
          {
            headers: {
              Accept: 'application/json'
            },
            auth: utils.authentication
          }
        ).then(res => {
          console.log('enabled');
        });
      } else {
        Axios.put(
          utils.apiUrl + 'routines/' + id + '/disable',
          {},
          {
            headers: {
              Accept: 'application/json'
            },
            auth: utils.authentication
          }
        ).then(res => {
          console.log('disabled');
        });
      }
    }
  }
};
</script>
