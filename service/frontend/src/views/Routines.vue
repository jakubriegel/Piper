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
        <SuggestionTable :house-loaded="houseLoaded" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import RoutineExpansionPanel from '@/components/RoutineExpansionPanel';
import SuggestionTable from '@/components/SuggestionTable';
import { axiosInstance } from '@/config/axiosInstance';
export default {
  name: 'Routines',
  components: { SuggestionTable, RoutineExpansionPanel },
  computed: {
    ...mapGetters('routines', ['routines'])
  },

  async mounted() {
    await this.getRooms();
    await this.getDeviceTypes();
    this.houseLoaded = true;
    this.getRoutines();
  },

  data: () => ({
    search: '',
    loading: true,
    houseLoaded: false,
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
    ...mapActions('house', ['getRooms', 'getDeviceTypes']),
    ...mapActions('routines', ['getRoutine', 'getRoutines', 'deleteRoutine']),

    async deleteItem(id) {
      await this.deleteRoutine(id);
      this.getRoutines();
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
        axiosInstance.put('routines/' + id + '/enable').then(res => {
          console.log('enabled');
        });
      } else {
        axiosInstance.put('routines/' + id + '/disable').then(res => {
          console.log('disabled');
        });
      }
    }
  }
};
</script>
