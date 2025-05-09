import { createRouter, createWebHistory } from 'vue-router';
import SignupView from '@/views/SignupView.vue';
import LoginView from '@/views/LoginView.vue';
import GoogleCallbackView from '@/views/GoogleCallbackView.vue';
import KakaoCallbackView from '@/views/KakaoCallbackView.vue';

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
    path: '/oauth/callback/google',
    component: GoogleCallbackView,
  },
  {
    path: '/oauth/callback/kakao',
    component: KakaoCallbackView,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
