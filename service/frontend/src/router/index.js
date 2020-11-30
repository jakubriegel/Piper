import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from '../views/Home.vue';
import About from '../views/About.vue';
import Routines from '@/views/Routines';
import EditRoutine from '@/views/EditRoutine';
import AddRoutine from '@/views/AddRoutine';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/routines',
    name: 'Routines',
    component: Routines
  },
  {
    path: '/routine/:id',
    name: 'Edit Routine',
    props: true,
    component: EditRoutine
  },
  {
    path: '/routine',
    name: 'Edit Routine',
    props: true,
    component: AddRoutine
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

export default router;
