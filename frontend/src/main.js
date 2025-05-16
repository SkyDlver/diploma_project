import './assets/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Import and initialize auth store
import { useAuthStore } from './stores/auth'

// Initialize the app
const authStore = useAuthStore()

authStore.init().finally(() => {
  app.mount('#app')

  // Set dark theme by default
  document.documentElement.classList.add('dark-theme')
})
