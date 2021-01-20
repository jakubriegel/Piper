import Axios from 'axios';
import { vueInstance } from '@/main';
export const axiosInstance = Axios.create({
  baseURL: 'https://jrie.eu:8001/',
  headers: {
    Accept: 'application/json'
  },
  auth: {
    username: 'owner-1',
    password: 'secret'
  }
});

axiosInstance.interceptors.response.use(
  response => response,
  error => {
    // error interceptor
    console.log(error);
    vueInstance.$toasted.error(error);
  }
);
