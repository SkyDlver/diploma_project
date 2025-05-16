// src/stores/auth.js
import { defineStore } from "pinia";
import { loginUser, registerUser, getCurrentUser } from "@/services/api";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    userFullName: (state) => state.user ? `${state.user.firstName} ${state.user.lastName}` : '',
  },

  actions: {
    async register(userData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await registerUser(userData);
        this.user = response.data;
        return response.data;
      } catch (err) {
        this.error = err.response?.data?.message || "Registration failed";
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async login(credentials) {
      this.loading = true;
      this.error = null;
      try {
        console.log("Auth store: Attempting login with:", credentials.email);
        const response = await loginUser(credentials.email, credentials.password);
        console.log("Auth store: Login successful", response.data);

        this.token = response.data.token;
        localStorage.setItem('token', this.token);

        // Fetch user profile after successful login
        await this.fetchCurrentUser();

        return response.data;
      } catch (err) {
        console.error("Auth store: Login failed", err);
        this.error = err.response?.data?.message || "Login failed";
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async fetchCurrentUser() {
      if (!this.token) return;

      this.loading = true;
      try {
        console.log("Auth store: Fetching current user");
        const response = await getCurrentUser();
        console.log("Auth store: User data received", response.data);
        this.user = response.data;
        return response.data;
      } catch (err) {
        console.error("Auth store: Failed to fetch user data", err);
        this.error = err.response?.data?.message || "Failed to fetch user data";
        // If we can't get user data, we should probably log out
        if (err.response?.status === 401) {
          this.logout();
        }
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      localStorage.removeItem('token');
      sessionStorage.removeItem("token");
    },

    // Initialize user data when app starts
    async init() {
      if (this.token && !this.user) {
        await this.fetchCurrentUser();
      }
    }
  },

  // Add this to persist the store between page reloads
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'auth',
        storage: localStorage,
        paths: ['token', 'user']
      }
    ]
  }
});
