<!-- src/App.vue -->
<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/layout/AppHeader.vue';
import AppFooter from '@/components/layout/AppFooter.vue';

const route = useRoute();

// Determine if the current route should show header/footer
const showHeaderFooter = computed(() => {
  // Don't show header/footer on welcome, login, register pages
  return !['/login', '/register', '/'].includes(route.path);
});
</script>

<template>
  <div id="app" :class="{ 'dark-theme': true }">
    <AppHeader v-if="showHeaderFooter" />

    <RouterView v-slot="{ Component }">
      <component :is="Component" />
    </RouterView>



    <AppFooter v-if="showHeaderFooter" />
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

:root {
  --primary-color: #4CAF50;
  --primary-dark: #45a049;
  --text-color: #333;
  --text-light: #666;
  --bg-color: #f9f9f9;
  --card-bg: #ffffff;
  --header-bg: #ffffff;
  --footer-bg: #2c3e50;
  --border-color: #e0e0e0;
  --shadow-color: rgba(0, 0, 0, 0.1);
}

.dark-theme {
  --primary-color: #66bb6a;
  --primary-dark: #4caf50;
  --text-color: #e0e0e0;
  --text-light: #b0b0b0;
  --bg-color: #121212;
  --card-bg: #1e1e1e;
  --header-bg: #1a1a1a;
  --footer-bg: #0f0f0f;
  --border-color: #333;
  --shadow-color: rgba(0, 0, 0, 0.3);
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Poppins', sans-serif;
  color: var(--text-color);
  background-color: var(--bg-color);
  line-height: 1.6;
  transition: background-color 0.3s, color 0.3s;
}


#app {
  min-height: 100vh; /* Ensure full height */
  display: flex;
  flex-direction: column;
}

/* Page transition animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Common styles */
.container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

button {
  cursor: pointer;
  font-family: 'Poppins', sans-serif;
}

a {
  text-decoration: none;
  color: var(--primary-color);
}

h1, h2, h3, h4, h5, h6 {
  color: var(--text-color);
  line-height: 1.3;
}

/* Responsive container */
@media (max-width: 1400px) {
  .container {
    max-width: 1200px;
  }
}

@media (max-width: 992px) {
  .container {
    max-width: 960px;
    padding: 0 1.5rem;
  }
}

@media (max-width: 768px) {
  .container {
    max-width: 720px;
    padding: 0 1rem;
  }
}

@media (max-width: 576px) {
  .container {
    max-width: 540px;
    padding: 0 1rem;
  }
}
</style>
