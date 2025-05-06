import { createRouter, createWebHashHistory } from 'vue-router';
import SignupView from '@/views/SignupView.vue';
import LoginView from '@/views/LoginView.vue';

const routes = [
  {
    path: '/signup',
    name: 'Signup',
    component: SignupView,
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
