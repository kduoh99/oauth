import { createRouter, createWebHistory } from 'vue-router';
import SignupView from '@/views/SignupView.vue';
import LoginView from '@/views/LoginView.vue';
import SocialCallbackView from '@/views/SocialCallbackView.vue';

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
  {
    path: '/oauth/callback/:provider',
    name: 'SocialCallback',
    component: SocialCallbackView,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
