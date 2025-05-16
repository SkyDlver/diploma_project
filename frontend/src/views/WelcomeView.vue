<!-- src/views/WelcomeView.vue -->
<script setup>
import { useRouter } from 'vue-router';
import { Icon } from '@iconify/vue';

const router = useRouter();

const navigateTo = (path) => {
  console.log(`Navigating to: ${path}`);
  router.push(path);
};
</script>

<template>
  <main class="welcome-container">
    <div class="welcome-content">
      <div class="logo-container">
        <img src="@/assets/logo.svg" alt="Kooking Logo" class="logo" />
        <h1>Kooking</h1>
      </div>

      <h2>Your Personal Recipe Assistant</h2>
      <p>Discover, save, and cook delicious recipes from around the world</p>

      <div class="button-container">
        <button @click="navigateTo('/login')" class="btn btn-primary">
          <Icon icon="mdi:login" /> Login
        </button>
        <button @click="navigateTo('/register')" class="btn btn-secondary">
          <Icon icon="mdi:account-plus" /> Register
        </button>
      </div>

      <div class="explore-section">
        <p>or explore without an account</p>
        <button @click="navigateTo('/dashboard')" class="btn btn-outline">
          <Icon icon="mdi:silverware-fork-knife" /> Explore Recipes <Icon icon="mdi:silverware-fork-knife" />
        </button>
      </div>
    </div>
  </main>
</template>

<style scoped>
.welcome-container {
  position: absolute;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #0a0a0a;
  overflow: hidden;
  padding: 2rem;
  inset: 0;
  z-index: 0;
  animation: gradientPulse 4s ease-in-out infinite;
}

.welcome-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 600px;
  width: 100%;
  padding: 3rem;
  border-radius: 24px;

  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow:
    0 0 30px rgba(0, 255, 200, 0.05),
    0 0 80px rgba(0, 200, 255, 0.08),
    inset 0 0 20px rgba(255, 255, 255, 0.03);
  animation: fadeIn 0.5s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.welcome-content h1,
.welcome-content h2 {
  color: #e0f7fa;
}

.welcome-content p {
  color: #b2dfdb;
}

.button-container {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  justify-content: center;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
}

.logo {
  width: 60px;
  height: 60px;
  margin-right: 1rem;
}

h1 {
  font-size: 3rem;
  color: var(--primary-color);
  margin: 0;
  font-weight: 700;
}

h2 {
  font-size: 1.8rem;
  color: #ffffff;
  margin-bottom: 1rem;
}

p {
  font-size: 1.2rem;
  color: #b0b0b0;
  margin-bottom: 2.5rem;
}

.button-container {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  margin-bottom: 2rem;
}

.btn {
  padding: 1rem 3.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn svg {
  width: 1.2rem;
  height: 1.2rem;
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-outline {
  background-color: transparent;
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 7px 14px rgba(0, 0, 0, 0.2);
}

.btn:active {
  transform: translateY(-1px);
}

.explore-section {
  margin-top: 2rem;
}

.explore-section p {
  margin-bottom: 1rem;
  font-size: 1rem;
  color: #888;
}

@media (max-width: 768px) {
  .welcome-content {
    padding: 2rem;
  }

  h1 {
    font-size: 2.5rem;
  }

  h2 {
    font-size: 1.5rem;
  }

  .button-container {
    flex-direction: column;
    gap: 1rem;
  }

  .btn {
    width: 100%;
  }
}
</style>
