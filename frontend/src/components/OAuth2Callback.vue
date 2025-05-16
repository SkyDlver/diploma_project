<template>
  <div class="oauth2-callback">
    <div class="loading-container">
      <div class="spinner"></div>
      <p>Processing your login...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

onMounted(async () => {
  const token = route.query.token;
  const error = route.query.error;

  if (token) {
    try {
      // Store the token in both localStorage and the auth store
      localStorage.setItem('token', token);
      authStore.token = token;

      // Fetch the user data using the token
      await authStore.fetchCurrentUser();

      console.log("OAuth2 login successful, user data fetched");

      // Redirect to dashboard
      router.push('/dashboard');
    } catch (err) {
      console.error("Error processing OAuth2 login:", err);
      router.push({
        path: '/login',
        query: { error: 'Failed to fetch user data after authentication' }
      });
    }
  } else if (error) {
    let errorMessage = 'Authentication failed';

    if (error === 'user_not_found') {
      errorMessage = 'No account found with this Google email. Please register first.';
    }

    // Redirect to login with error
    router.push({
      path: '/login',
      query: { error: errorMessage }
    });
  } else {
    // Fallback redirect
    router.push('/login');
  }
});
</script>

<style scoped>
.oauth2-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f8f9fa;
}

.loading-container {
  text-align: center;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #09f;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
