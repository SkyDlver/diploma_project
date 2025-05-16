// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from '@/stores/auth';

const routes = [
  {
    path: '/',
    name: 'Welcome',
    component: () => import('@/views/WelcomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/oauth2/callback',
    name: 'OAuth2Callback',
    component: () => import('@/components/OAuth2Callback.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/UserProfileView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/recipe/:id',
    name: 'RecipeDetail',
    component: () => import('@/views/RecipeDetailView.vue'),
    props: true
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/KookingDashboard.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/recipes',
    name: 'Recipes',
    component: () => import('@/views/RecipesView.vue')
  },
  {
    path: '/search',
    name: 'RecipeSearch',
    component: () => import('@/views/SearchView.vue')
  },
  {
    path: '/category/:category',
    name: 'Category',
    component: () => import('@/views/CategoryView.vue')
  },
  {
    path: '/collection/:type',
    name: 'RecipeCollection',
    component: () => import('@/views/CollectionView.vue')
  },
  {
    path: '/recipes/advanced-search',
    name: 'AdvancedSearch',
    component: () => import('@/views/AdvancedSearchView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/Ingredients',
    name: 'ingredients',
    component: () => import('@/views/IngredientsView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/my-recipes',
    name: 'my-recipes',
    component: () => import('@/views/MyRecipesView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/shopping-list',
    name: 'ShoppingList',
    component: () => import('@/views/ShoppingListView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/favorites',
    name: 'favorites',
    component: () => import('@/views/FavoriteRecipesView.vue'),
    meta: { requiresAuth: false }
  }
  // Add other routes as needed
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  }
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;

  // For debugging
  console.log(`Navigating to: ${to.path}, Auth status: ${isAuthenticated}`);

  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('Authentication required, redirecting to login');
    next('/login');
  } else if (to.meta.requiresGuest && isAuthenticated) {
    console.log('Already authenticated, redirecting to dashboard');
    next('/dashboard');
  } else {
    next();
  }
});

router.onError((error) => {
  console.error('Router error:', error);
  if (error.message.includes('Failed to fetch dynamically imported module')) {
    window.location.reload();
  }
});
export default router;
