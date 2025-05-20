import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    host: true,
    allowedHosts: ['myfrontend.zrok.defgun.blog'],
    port: 8080,
    proxy: {
      "/api": {
        // target: "https://mybackend.zrok.defgun.blog",
        target : "https://localhost:8098",
        changeOrigin: true,
        secure: false,
      },
    },
    cors: true
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
    outDir: 'target/dist',
    assetsDir: 'static',
    emptyOutDir: true,
  }
})
