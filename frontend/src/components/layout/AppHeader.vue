<!-- src/components/layout/AppHeader.vue -->
<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img src="@/assets/logo.svg" alt="Kooking" />
            <span>Kooking</span>
          </router-link>
        </div>

        <nav class="main-nav" :class="{ 'active': mobileMenuOpen }">
          <ul>
            <li><router-link to="/dashboard">Home</router-link></li>
            <li><router-link to="/recipes">Recipes</router-link></li>
            <li><router-link to="/recipes/advanced-search">AdvancedSearch</router-link></li>
            <li><router-link to="/Ingredients">Ingredients</router-link></li>
          </ul>
        </nav>

        <div class="header-actions">
          <div class="search-bar">
            <input
              type="text"
              placeholder="Search recipes..."
              v-model="searchQuery"
              @keyup.enter="handleSearch"
            />
            <button @click="handleSearch">
              <i class="fas fa-search"></i>
            </button>
          </div>

          <div class="user-menu" v-if="isAuthenticated">
            <button class="user-button" @click="toggleUserDropdown">
              <img :src="userAvatar" alt="User" class="user-avatar" />
              <span class="user-name">{{ userName }}</span>
              <i class="fas fa-chevron-down"></i>
            </button>
            <div class="dropdown-menu" v-if="userDropdownOpen">
              <router-link to="/profile">My Profile</router-link>
              <router-link to="/favorites">Favorites</router-link>
              <router-link to="/my-recipes">My Recipes</router-link>
              <router-link to="/shopping-list">Shopping List</router-link>
              <a href="#" @click.prevent="logout">Logout</a>
            </div>
          </div>

          <div class="auth-buttons" v-else>
            <router-link to="/login" class="btn btn-outline">Login</router-link>
            <router-link to="/register" class="btn btn-primary">Sign Up</router-link>
          </div>

          <button class="theme-toggle" @click="toggleTheme">
            <i class="fas" :class="isDarkTheme ? 'fa-sun' : 'fa-moon'"></i>
          </button>

          <button class="mobile-menu-toggle" @click="toggleMobileMenu">
            <i class="fas fa-bars" v-if="!mobileMenuOpen"></i>
            <i class="fas fa-times" v-else></i>
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const searchQuery = ref('');
const mobileMenuOpen = ref(false);
const userDropdownOpen = ref(false);
const isDarkTheme = ref(true); // Default to dark theme

const isAuthenticated = computed(() => authStore.isAuthenticated);
const userName = computed(() => authStore.user?.firstName || 'User');
const userAvatar = computed(() => authStore.user?.avatar || 'https://ui-avatars.com/api/?name=' + userName.value + '&background=4CAF50&color=fff');

watch(() => authStore.user, (newUser) => {
  console.log("User data changed in header:", newUser);
}, { deep: true });

onMounted(() => {
  console.log("Header mounted, user data:", authStore.user);
  if (authStore.isAuthenticated && !authStore.user) {
    authStore.fetchCurrentUser();
  }
});

const handleSearch = () => {
  if (searchQuery.value) {
    router.push({ path: '/recipes/advanced-search', query: { search: searchQuery.value } });
    searchQuery.value = '';
    mobileMenuOpen.value = false;
  }
};

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
  if (mobileMenuOpen.value) {
    userDropdownOpen.value = false;
  }
};

const toggleUserDropdown = () => {
  userDropdownOpen.value = !userDropdownOpen.value;
};

const toggleTheme = () => {
  isDarkTheme.value = !isDarkTheme.value;
  document.documentElement.classList.toggle('dark-theme', isDarkTheme.value);
};

const logout = () => {
  authStore.logout();
  router.push('/');
  userDropdownOpen.value = false;
};
</script>

<style scoped>
.app-header {
  background-color: var(--header-bg);
  box-shadow: 0 2px 10px var(--shadow-color);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: background-color 0.3s, box-shadow 0.3s;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--primary-color);
  font-weight: 700;
  font-size: 1.5rem;
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.main-nav ul {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}

.main-nav li {
  margin: 0 1.2rem;
}

.main-nav a {
  text-decoration: none;
  color: var(--text-color);
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.3s;
}

.main-nav a:hover {
  color: var(--primary-color);
}

.main-nav a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 0;
  background-color: var(--primary-color);
  transition: width 0.3s;
}

.main-nav a:hover::after,
.main-nav a.router-link-active::after {
  width: 100%;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.search-bar {
  position: relative;
}

.search-bar input {
  padding: 0.6rem 2.5rem 0.6rem 1.2rem;
  border: 1px solid var(--border-color);
  border-radius: 50px;
  width: 220px;
  background-color: var(--card-bg);
  color: var(--text-color);
  transition: all 0.3s;
}

.search-bar input:focus {
  outline: none;
  border-color: var(--primary-color);
  width: 280px;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.search-bar button {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--text-light);
  cursor: pointer;
}

.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  color: var(--text-color);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 8px;
  border: 2px solid var(--primary-color);
}

.user-name {
  margin-right: 5px;
  font-weight: 500;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: var(--card-bg);
  border-radius: 8px;
  box-shadow: 0 5px 15px var(--shadow-color);
  width: 200px;
  padding: 0.5rem 0;
  z-index: 10;
  animation: fadeInDown 0.3s ease;
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.dropdown-menu a {
  display: block;
  padding: 0.75rem 1.2rem;
  text-decoration: none;
  color: var(--text-color);
  transition: background-color 0.2s;
}

.dropdown-menu a:hover {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 50px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
}

.btn-outline {
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  background: transparent;
}

.btn-outline:hover {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
  border: 1px solid var(--primary-color);
}

.btn-primary:hover {
  background-color: var(--primary-dark);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.2);
}

.theme-toggle {
  background: none;
  border: none;
  color: var(--text-color);
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.3s;
}

.theme-toggle:hover {
  color: var(--primary-color);
}

.mobile-menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-color);
}

@media (max-width: 1200px) {
  .container {
    padding: 0 1.5rem;
  }

  .main-nav li {
    margin: 0 1rem;
  }
}

@media (max-width: 992px) {
  .main-nav {
    position: fixed;
    top: 80px;
    left: 0;
    width: 100%;
    height: 0;
    background-color: var(--header-bg);
    overflow: hidden;
    transition: height 0.3s ease;
    box-shadow: 0 5px 10px var(--shadow-color);
    z-index: 99;
  }

  .main-nav.active {
    height: auto;
  }

  .main-nav ul {
    flex-direction: column;
    padding: 1rem 0;
  }

  .main-nav li {
    margin: 0;
  }

  .main-nav a {
    display: block;
    padding: 1rem 2rem;
  }

  .search-bar {
    display: none;
  }

  .mobile-menu-toggle {
    display: block;
  }

  .auth-buttons .btn-outline {
    display: none;
  }
}

@media (max-width: 576px) {
  .user-name {
    display: none;
  }

  .header-content {
    height: 70px;
  }

  .logo img {
    height: 32px;
  }

  .logo a {
    font-size: 1.3rem;
  }

  .main-nav {
    top: 70px;
  }
}
</style>
