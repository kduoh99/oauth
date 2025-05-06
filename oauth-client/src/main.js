import { createApp } from 'vue';
import App from './App.vue';
import vutify from './plugins/vuetify';
import router from '@/router/index.js';

const app = createApp(App);

app.use(vutify);
app.use(router);
app.mount('#app');
