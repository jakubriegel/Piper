import { axiosInstance } from '@/config/axiosInstance';

export const house = {
  namespaced: true,
  state: {
    rooms: [],
    devices: {},
    roomsDict: {},
    deviceTypes: []
  },

  getters: {
    rooms: state => state.rooms,
    devices: state => state.devices,
    deviceTypes: state => state.deviceTypes,
    devicesInRoom: state => id => state.devices[id],
    roomName: state => id => state.roomsDict[id],
    roomIds: state => Object.keys(state.roomsDict),
    eventsForDeviceType: state => id => state.deviceTypes[id],
    deviceTypeByDeviceId: state => id =>
      Object.values(state.devices)
        .flat()
        .reduce((a, x) => ({ ...a, [x.id]: x.typeId }), {})[id]
  },

  actions: {
    getRooms({ commit, dispatch }) {
      axiosInstance.get('houses/rooms').then(res => {
        commit('SET_ROOMS', res.data.rooms);
        commit('SET_ROOMS_DICT', res.data.rooms);
        for (let room of res.data.rooms) {
          dispatch('getDevices', room);
        }
      });
    },

    getDevices({ commit }, room) {
      axiosInstance.get('houses/devices?roomId=' + room.id).then(res => {
        commit('ADD_DEVICES_PER_ROOM', {
          roomId: room.id,
          devicesInRoom: res.data.devices
        });
      });
    },

    getDeviceTypes({ commit }) {
      axiosInstance.get('houses/devices/types').then(res => {
        commit('SET_DEVICE_TYPES', res.data.types);
      });
    }
  },
  mutations: {
    SET_ROOMS(state, rooms) {
      state.rooms = rooms.map(room => ({ roomId: room.id, name: room.name }));
    },

    SET_ROOMS_DICT(state, rooms) {
      state.roomsDict = rooms.reduce((a, x) => ({ ...a, [x.id]: x.name }), {});
    },

    SET_DEVICE_TYPES(state, deviceTypes) {
      state.deviceTypes = deviceTypes.reduce(
        (a, x) => ({ ...a, [x.id]: x.events }),
        {}
      );
    },

    ADD_DEVICES_PER_ROOM(state, payload) {
      state.devices[payload.roomId] = payload.devicesInRoom;
    }
  }
};
