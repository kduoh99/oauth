<template>
  <div>구글 로그인 진행중..</div>
</template>

<script>
  import axios from 'axios';

  export default {
    created() {
      const code = new URL(window.location.href).searchParams.get('code');
      console.log(code);
      this.sendCode(code);
    },
    methods: {
      async sendCode(code) {
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/api/member/callback/google?code=${code}`,
        );
        const accessToken = response.data.accessToken;
        localStorage.setItem('accessToken', accessToken);
        window.location.href = '/';
      },
    },
  };
</script>
