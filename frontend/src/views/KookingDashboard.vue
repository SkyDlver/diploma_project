<!-- src/views/KookingDashboard.vue -->
<template>
  <div class="dashboard">
    <main class="dashboard-content">
      <!-- Hero Section with Search -->
      <section class="hero-section">
        <div class="hero-content">
          <h1>Discover <span class="highlight">Delicious</span> Recipes</h1>
          <p>Find and save your favorite recipes from around the world</p>
          <div class="search-container">
            <input
                type="text"
                v-model="searchQuery"
                placeholder="Search for recipes, ingredients, or cuisines..."
                @keyup.enter="searchRecipes"
            />
            <button @click="searchRecipes" class="search-button">
              <i class="fas fa-search"></i> Search
            </button>
          </div>
          <div class="quick-tags">
            <span>Popular:</span>
            <a href="#" @click.prevent="quickSearch('pasta')">Pasta</a>
            <a href="#" @click.prevent="quickSearch('chicken')">Chicken</a>
            <a href="#" @click.prevent="quickSearch('vegetarian')">Vegetarian</a>
            <a href="#" @click.prevent="quickSearch('dessert')">Dessert</a>
          </div>
          <div class="advanced-search-link">
            <router-link to="/recipes/advanced-search">Advanced Search</router-link>
          </div>
        </div>
      </section>

      <!-- Quick Categories -->
      <section class="categories-section">
        <div class="section-header">
          <h2>Explore Categories</h2>
          <router-link to="/categories" class="view-all">View All</router-link>
        </div>
        <div class="categories-grid">
          <div class="category-card" v-for="category in categories" :key="category.id">
            <a href="#" @click.prevent="navigateToCategory(category)">
              <div class="category-image">
                <img :src="category.image" :alt="category.name" />
              </div>
              <h3>{{ category.name }}</h3>
            </a>
          </div>

          <!-- Loading placeholders for categories -->
          <div v-if="loadingCategories" class="category-card loading-card" v-for="i in 6" :key="'loading-cat-'+i">
            <div class="loading-image"></div>
            <div class="loading-title"></div>
          </div>
        </div>
      </section>

      <!-- Trending Recipes Section -->
      <section class="recipes-section">
        <div class="section-header">
          <h2>Trending Now</h2>
          <router-link to="/recipes/trending" class="view-all">View All</router-link>
        </div>
        <div class="carousel-container">
          <button class="carousel-btn prev" @click="scrollCarousel('trending', -1)" aria-label="Previous" v-if="trendingRecipes.length > 0">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="recipes-carousel" ref="trendingCarousel">
            <div class="recipe-card" v-for="recipe in trendingRecipes" :key="recipe.id" @click="showRecipeBrief(recipe)">
              <div class="recipe-image">
                <img :src="recipe.imageUrl" :alt="recipe.name" />
                <div class="recipe-time">
                  <i class="far fa-clock"></i> {{ recipe.cookingTime }} min
                </div>
              </div>
              <div class="recipe-info">
                <div class="recipe-tags">
                  <span>{{ recipe.cuisine }}</span>
                  <span>{{ recipe.mealType }}</span>
                </div>
                <h3>{{ recipe.name }}</h3>
                <div class="recipe-rating">
                  <div class="stars">
                    <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'star-'+i"></i>
                    <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                    <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'empty-'+i"></i>
                  </div>
                  <span>{{ recipe.rating.toFixed(1) }}</span>
                </div>
              </div>
            </div>

            <!-- Loading placeholders -->
            <div v-if="loadingTrending" class="recipe-card loading-card" v-for="i in 4" :key="'loading-trend-'+i">
              <div class="loading-image"></div>
              <div class="loading-content">
                <div class="loading-tags"></div>
                <div class="loading-title"></div>
                <div class="loading-rating"></div>
              </div>
            </div>

            <!-- Empty state -->
            <div v-if="!loadingTrending && trendingRecipes.length === 0" class="empty-state">
              <i class="fas fa-utensils"></i>
              <p>No trending recipes found</p>
            </div>
          </div>
          <button class="carousel-btn next" @click="scrollCarousel('trending', 1)" aria-label="Next" v-if="trendingRecipes.length > 0">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <!-- Recommended Recipes Section -->
      <section class="recipes-section">
        <div class="section-header">
          <h2>Recommended for You</h2>
          <router-link to="/recipes/recommended" class="view-all">View All</router-link>
        </div>
        <div class="carousel-container">
          <button class="carousel-btn prev" @click="scrollCarousel('recommended', -1)" aria-label="Previous" v-if="recommendedRecipes.length > 0">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="recipes-carousel" ref="recommendedCarousel">
            <div class="recipe-card" v-for="recipe in recommendedRecipes" :key="recipe.id" @click="showRecipeBrief(recipe)">
              <div class="recipe-image">
                <img :src="recipe.imageUrl" :alt="recipe.name" />
                <div class="recipe-time">
                  <i class="far fa-clock"></i> {{ recipe.cookingTime }} min
                </div>
              </div>
              <div class="recipe-info">
                <div class="recipe-tags">
                  <span>{{ recipe.cuisine }}</span>
                  <span>{{ recipe.mealType }}</span>
                </div>
                <h3>{{ recipe.name }}</h3>
                <div class="recipe-rating">
                  <div class="stars">
                    <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'star-'+i"></i>
                    <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                    <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'empty-'+i"></i>
                  </div>
                  <span>{{ recipe.rating.toFixed(1) }}</span>
                </div>
              </div>
            </div>

            <!-- Loading placeholders -->
            <div v-if="loadingRecommended" class="recipe-card loading-card" v-for="i in 4" :key="'loading-rec-'+i">
              <div class="loading-image"></div>
              <div class="loading-content">
                <div class="loading-tags"></div>
                <div class="loading-title"></div>
                <div class="loading-rating"></div>
              </div>
            </div>

            <!-- Empty state -->
            <div v-if="!loadingRecommended && recommendedRecipes.length === 0" class="empty-state">
              <i class="fas fa-utensils"></i>
              <p>No recommended recipes found</p>
            </div>
          </div>
          <button class="carousel-btn next" @click="scrollCarousel('recommended', 1)" aria-label="Next" v-if="recommendedRecipes.length > 0">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <!-- Seasonal Recipes Section -->
      <section class="recipes-section">
        <div class="section-header">
          <h2>Seasonal Favorites</h2>
          <router-link to="/recipes/seasonal" class="view-all">View All</router-link>
        </div>
        <div class="carousel-container">
          <button class="carousel-btn prev" @click="scrollCarousel('seasonal', -1)" aria-label="Previous" v-if="seasonalRecipes.length > 0">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="recipes-carousel" ref="seasonalCarousel">
            <div class="recipe-card" v-for="recipe in seasonalRecipes" :key="recipe.id" @click="showRecipeBrief(recipe)">
              <div class="recipe-image">
                <img :src="recipe.imageUrl" :alt="recipe.name" />
                <div class="recipe-time">
                  <i class="far fa-clock"></i> {{ recipe.cookingTime }} min
                </div>
              </div>
              <div class="recipe-info">
                <div class="recipe-tags">
                  <span>{{ recipe.cuisine }}</span>
                  <span>{{ recipe.mealType }}</span>
                </div>
                <h3>{{ recipe.name }}</h3>
                <div class="recipe-rating">
                  <div class="stars">
                    <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'star-'+i"></i>
                    <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                    <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'empty-'+i"></i>
                  </div>
                  <span>{{ recipe.rating.toFixed(1) }}</span>
                </div>
              </div>
            </div>

            <!-- Loading placeholders -->
            <div v-if="loadingSeasonal" class="recipe-card loading-card" v-for="i in 4" :key="'loading-seas-'+i">
              <div class="loading-image"></div>
              <div class="loading-content">
                <div class="loading-tags"></div>
                <div class="loading-title"></div>
                <div class="loading-rating"></div>
              </div>
            </div>

            <!-- Empty state -->
            <div v-if="!loadingSeasonal && seasonalRecipes.length === 0" class="empty-state">
              <i class="fas fa-utensils"></i>
              <p>No seasonal recipes found</p>
            </div>
          </div>
          <button class="carousel-btn next" @click="scrollCarousel('seasonal', 1)" aria-label="Next" v-if="seasonalRecipes.length > 0">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <!-- Newsletter Section -->
      <section class="newsletter-section">
        <div class="newsletter-content">
          <div class="newsletter-text">
            <h2>Get Weekly Recipe Inspiration</h2>
            <p>Subscribe to our newsletter and receive the latest recipes, cooking tips, and exclusive content.</p>
          </div>
          <form @submit.prevent="subscribeNewsletter" class="newsletter-form">
            <input
                type="email"
                placeholder="Your email address"
                v-model="newsletterEmail"
                required
            />
            <button type="submit">Subscribe</button>
          </form>
        </div>
      </section>
    </main>

    <!-- Recipe Brief Modal -->
    <div class="modal-overlay" v-if="showModal" @click="closeModal">
      <div class="recipe-brief-modal" @click.stop>
        <button class="close-modal" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>

        <div v-if="selectedRecipe && !loadingRecipeBrief" class="recipe-brief-content">
          <div class="recipe-brief-image">
            <img :src="recipeBrief.imageUrl" :alt="recipeBrief.name" />
          </div>
          <div class="recipe-brief-info">
            <h2>{{ recipeBrief.name }}</h2>

            <div class="recipe-brief-meta">
              <div class="recipe-brief-tags">
                <span>{{ recipeBrief.cuisine }}</span>
                <span>{{ recipeBrief.mealType }}</span>
              </div>
              <div class="recipe-brief-time">
                <i class="far fa-clock"></i> {{ recipeBrief.cookingTime }} min
              </div>
            </div>

            <div class="recipe-brief-rating">
              <div class="stars">
                <i class="fas fa-star" v-for="i in Math.floor(recipeBrief.rating)" :key="'modal-star-'+i"></i>
                <i class="fas fa-star-half-alt" v-if="recipeBrief.rating % 1 >= 0.5"></i>
                <i class="far fa-star" v-for="i in (5 - Math.ceil(recipeBrief.rating))" :key="'modal-empty-'+i"></i>
              </div>
              <span>{{ recipeBrief.rating.toFixed(1) }}</span>
            </div>

            <p class="recipe-brief-description">{{ recipeBrief.description }}</p>

            <div class="recipe-brief-author" v-if="recipeBrief.author">
              <p>By: {{ recipeBrief.author.firstName }} {{ recipeBrief.author.lastName }}</p>
            </div>

            <div class="recipe-brief-actions">
              <button class="btn-favorite" @click="toggleFavorite" :class="{ 'is-favorite': isFavorite }">
                <i class="fas" :class="isFavorite ? 'fa-heart' : 'fa-heart'"></i>
                {{ isFavorite ? 'Favorited' : 'Add to Favorites' }}
              </button>
              <router-link :to="`/recipe/${selectedRecipe.id}`" class="btn-view-recipe">
                View Full Recipe
              </router-link>
            </div>
          </div>
        </div>

        <!-- Loading state for recipe brief -->
        <div v-if="loadingRecipeBrief" class="recipe-brief-loading">
          <div class="loading-spinner"></div>
          <p>Loading recipe details...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  getTrendingRecipes,
  getRecommendedRecipes,
  getSeasonalRecipes,
  getRecipeBrief,
  favoriteRecipe,
  unfavoriteRecipe,
  searchRecipes as apiSearchRecipes
} from '@/services/api';

const router = useRouter();
const authStore = useAuthStore();
const searchQuery = ref('');
const newsletterEmail = ref('');

// Carousel refs
const trendingCarousel = ref(null);
const recommendedCarousel = ref(null);
const seasonalCarousel = ref(null);

// Recipe data
const trendingRecipes = ref([]);
const recommendedRecipes = ref([]);
const seasonalRecipes = ref([]);

// Loading states
const loadingTrending = ref(true);
const loadingRecommended = ref(true);
const loadingSeasonal = ref(true);
const loadingCategories = ref(true);

// Modal state
const showModal = ref(false);
const selectedRecipe = ref(null);
const recipeBrief = ref({});
const loadingRecipeBrief = ref(false);
const isFavorite = ref(false);

// Check if user is authenticated
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Categories - will be fetched from backend in a real implementation
const categories = ref([
  { id: 'breakfast', name: 'Breakfast', slug: 'breakfast', image: 'https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', mealType: 'BREAKFAST' },
  { id: 'lunch', name: 'Lunch', slug: 'lunch', image: 'https://images.unsplash.com/photo-1547496502-affa22d38842?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', mealType: 'LUNCH' },
  { id: 'dinner', name: 'Dinner', slug: 'dinner', image: 'https://images.unsplash.com/photo-1576402187878-974f70c890a5?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', mealType: 'DINNER' },
  { id: 'desserts', name: 'Desserts', slug: 'desserts', image: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', dietType: 'DESSERT' },
  { id: 'vegetarian', name: 'Vegetarian', slug: 'vegetarian', image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', dietType: 'VEGETARIAN' },
  { id: 'quick', name: 'Quick & Easy', slug: 'quick-easy', image: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80', maxCookingTime: 30 },
]);

// Fetch trending recipes
const fetchTrendingRecipes = async () => {
  loadingTrending.value = true;
  try {
    const response = await getTrendingRecipes();
    trendingRecipes.value = response.data;
    console.log('Trending recipes:', trendingRecipes.value);
  } catch (error) {
    console.error('Error fetching trending recipes:', error);
  } finally {
    loadingTrending.value = false;
  }
};

// Fetch recommended recipes
const fetchRecommendedRecipes = async () => {
  loadingRecommended.value = true;
  try {
    const response = await getRecommendedRecipes();
    recommendedRecipes.value = response.data;
    console.log('Recommended recipes:', recommendedRecipes.value);
  } catch (error) {
    console.error('Error fetching recommended recipes:', error);
  } finally {
    loadingRecommended.value = false;
  }
};

// Fetch seasonal recipes
const fetchSeasonalRecipes = async () => {
  loadingSeasonal.value = true;
  try {
    const response = await getSeasonalRecipes();
    seasonalRecipes.value = response.data;
    console.log('Seasonal recipes:', seasonalRecipes.value);
  } catch (error) {
    console.error('Error fetching seasonal recipes:', error);
  } finally {
    loadingSeasonal.value = false;
  }
};

// Fetch categories - in a real implementation, this would come from the backend
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    // This would be an API call in a real implementation
    // const response = await getCategories();
    // categories.value = response.data;

    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
    loadingCategories.value = false;
  } catch (error) {
    console.error('Error fetching categories:', error);
    loadingCategories.value = false;
  }
};

// Search recipes
const searchRecipes = () => {
  if (searchQuery.value) {
    router.push({
      path: '/recipes/search',
      query: { q: searchQuery.value }
    });
  }
};

// Quick search
const quickSearch = (term) => {
  router.push({
    path: '/recipes/search',
    query: { q: term }
  });
};

// Navigate to category
const navigateToCategory = (category) => {
  // Build query parameters based on category type
  const query = {};

  if (category.mealType) {
    query.mealTypes = [category.mealType];
  }

  if (category.dietType) {
    query.dietTypes = [category.dietType];
  }

  if (category.maxCookingTime) {
    query.maxCookingTime = category.maxCookingTime;
  }

  router.push({
    path: '/recipes/advanced-search',
    query
  });
};

// Carousel scroll
const scrollCarousel = (type, direction) => {
  const carousel = {
    'trending': trendingCarousel.value,
    'recommended': recommendedCarousel.value,
    'seasonal': seasonalCarousel.value
  }[type];

  if (carousel) {
    const scrollAmount = carousel.offsetWidth * 0.8 * direction;
    carousel.scrollBy({ left: scrollAmount, behavior: 'smooth' });
  }
};

// Show recipe brief modal
const showRecipeBrief = async (recipe) => {
  selectedRecipe.value = recipe;
  showModal.value = true;
  loadingRecipeBrief.value = true;

  try {
    const response = await getRecipeBrief(recipe.id);
    recipeBrief.value = response.data;
    // Check if recipe is favorited - this would typically come from the API
    // For now, we'll just set it to false
    isFavorite.value = false;
  } catch (error) {
    console.error('Error fetching recipe brief:', error);
  } finally {
    loadingRecipeBrief.value = false;
  }
};

// Close modal
const closeModal = () => {
  showModal.value = false;
  selectedRecipe.value = null;
};

// Toggle favorite
const toggleFavorite = async () => {
  if (!isAuthenticated.value) {
    // Redirect to login if not authenticated
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    });
    return;
  }

  try {
    if (isFavorite.value) {
      await unfavoriteRecipe(selectedRecipe.value.id);
      isFavorite.value = false;
    } else {
      await favoriteRecipe(selectedRecipe.value.id);
      isFavorite.value = true;
    }
  } catch (error) {
    console.error('Error toggling favorite:', error);
  }
};

// Newsletter subscription
const subscribeNewsletter = () => {
  alert(`Thank you for subscribing with ${newsletterEmail.value}!`);
  newsletterEmail.value = '';
};

// Fetch all data on component mount
onMounted(() => {
  fetchTrendingRecipes();
  fetchRecommendedRecipes();
  fetchSeasonalRecipes();
  fetchCategories();
});
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: var(--bg-color);
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.dashboard-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)),
  url('https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixlib=rb-1.2.1&auto=format&fit=crop&w=1500&q=80');
  background-size: cover;
  background-position: center;
  color: white;
  padding: 6rem 2rem;
  border-radius: 16px;
  margin-bottom: 3rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  color: white;
}

.hero-content .highlight {
  color: var(--primary-color);
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  color: rgba(255, 255, 255, 0.9);
}

.search-container {
  display: flex;
  max-width: 600px;
  margin: 0 auto 1.5rem;
}

.search-container input {
  flex: 1;
  padding: 1rem 1.5rem;
  border: none;
  border-radius: 50px 0 0 50px;
  font-size: 1rem;
  background-color: rgba(255, 255, 255, 0.9);
}

.search-container input:focus {
  outline: none;
  background-color: white;
}

.search-button {
  padding: 1rem 1.5rem;
  border: none;
  border-radius: 0 50px 50px 0;
  background-color: var(--primary-color);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: var(--primary-dark);
}

.quick-tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 1rem;
}

.quick-tags span {
  color: rgba(255, 255, 255, 0.8);
}

.quick-tags a {
  color: white;
  text-decoration: none;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 0.3rem 0.8rem;
  border-radius: 50px;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.quick-tags a:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
}

.advanced-search-link {
  margin-top: 1rem;
}

.advanced-search-link a {
  color: white;
  text-decoration: underline;
  font-weight: 500;
  transition: all 0.3s;
}

.advanced-search-link a:hover {
  color: var(--primary-color);
}

/* Section Headers */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 1.8rem;
  color: var(--text-color);
  margin: 0;
}

.view-all {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.view-all:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

/* Categories Section */
.categories-section {
  margin-bottom: 3rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.5rem;
}

.category-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: var(--card-bg);
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px var(--shadow-color);
}

.category-card a {
  text-decoration: none;
  color: var(--text-color);
}

.category-image {
  height: 120px;
  overflow: hidden;
}

.category-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.category-card:hover .category-image img {
  transform: scale(1.1);
}

.category-card h3 {
  padding: 1rem;
  margin: 0;
  text-align: center;
  font-size: 1.1rem;
}

/* Recipe Carousels */
.recipes-section {
  margin-bottom: 3rem;
}

.carousel-container {
  position: relative;
}

.recipes-carousel {
  display: flex;
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  gap: 1.5rem;
  padding: 0.5rem 0;
  min-height: 300px; /* Ensure space for loading/empty states */
}

.recipes-carousel::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--card-bg);
  border: none;
  box-shadow: 0 2px 10px var(--shadow-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: all 0.3s;
  color: var(--text-color);
}

.carousel-btn:hover {
  background-color: var(--primary-color);
  color: white;
}

.carousel-btn.prev {
  left: -20px;
}

.carousel-btn.next {
  right: -20px;
}

.recipe-card {
  flex: 0 0 calc(25% - 1.5rem);
  min-width: 250px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: var(--card-bg);
  cursor: pointer;
}

.recipe-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px var(--shadow-color);
}

.recipe-image {
  height: 180px;
  position: relative;
  overflow: hidden;
}

.recipe-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.recipe-card:hover .recipe-image img {
  transform: scale(1.1);
}

.recipe-time {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.3rem 0.6rem;
  border-radius: 50px;
  font-size: 0.8rem;
}

.recipe-info {
  padding: 1rem;
}

.recipe-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 0.5rem;
}

.recipe-tags span {
  background-color: rgba(76, 175, 80, 0.1);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: var(--primary-color);
}

.recipe-card h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recipe-rating {
  display: flex;
  align-items: center;
}

.stars {
  color: #FFD700;
  margin-right: 5px;
}

.stars i {
  font-size: 0.9rem;
}

/* Loading placeholders */
.loading-card {
  animation: pulse 1.5s infinite;
}

.loading-image {
  height: 180px;
  background-color: rgba(255, 255, 255, 0.1);
}

.loading-content {
  padding: 1rem;
}

.loading-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 0.5rem;
}

.loading-tags::before, .loading-tags::after {
  content: '';
  height: 20px;
  width: 60px;
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.1);
}

.loading-title {
  height: 20px;
  margin-bottom: 0.5rem;
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.1);
}

.loading-rating {
  height: 16px;
  width: 100px;
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.1);
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 0.8; }
  100% { opacity: 0.6; }
}

/* Empty state */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: var(--text-light);
  text-align: center;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

/* Newsletter Section */
.newsletter-section {
  background-color: var(--card-bg);
  padding: 3rem 2rem;
  border-radius: 16px;
  margin-bottom: 3rem;
  box-shadow: 0 4px 15px var(--shadow-color);
}

.newsletter-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.newsletter-text {
  flex: 1;
}

.newsletter-text h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.8rem;
  color: var(--text-color);
}

.newsletter-text p {
  margin: 0;
  color: var(--text-light);
  line-height: 1.6;
}

.newsletter-form {
  flex: 1;
  display: flex;
  gap: 1rem;
}

.newsletter-form input {
  flex: 1;
  padding: 0.8rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 1rem;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.newsletter-form input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.newsletter-form button {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  background-color: var(--primary-color);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.newsletter-form button:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* Recipe Brief Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
}

.recipe-brief-modal {
  background-color: var(--card-bg);
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.close-modal {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.2);
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s;
}

.close-modal:hover {
  background: rgba(0, 0, 0, 0.4);
  transform: scale(1.1);
}

.recipe-brief-content {
  display: flex;
  flex-direction: column;
}

.recipe-brief-image {
  height: 300px;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
}

.recipe-brief-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recipe-brief-info {
  padding: 2rem;
}

.recipe-brief-info h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.8rem;
  color: var(--text-color);
}

.recipe-brief-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.recipe-brief-tags {
  display: flex;
  gap: 8px;
}

.recipe-brief-tags span {
  background-color: rgba(76, 175, 80, 0.1);
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  color: var(--primary-color);
}

.recipe-brief-time {
  color: var(--text-light);
  font-size: 0.9rem;
}

.recipe-brief-rating {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.recipe-brief-description {
  margin-bottom: 1.5rem;
  line-height: 1.6;
  color: var(--text-color);
}

.recipe-brief-author {
  margin-bottom: 1.5rem;
  font-style: italic;
  color: var(--text-light);
}

.recipe-brief-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-favorite, .btn-view-recipe {
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  flex: 1;
}

.btn-favorite {
  background-color: white;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-favorite:hover {
  background-color: rgba(76, 175, 80, 0.1);
}

.btn-favorite.is-favorite {
  background-color: var(--primary-color);
  color: white;
}

.btn-view-recipe {
  background-color: var(--primary-color);
  border: 1px solid var(--primary-color);
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-view-recipe:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* Loading spinner for recipe brief */
.recipe-brief-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid rgba(76, 175, 80, 0.1);
  border-top: 5px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive Styles */
@media (max-width: 1200px) {
  .recipe-card {
    flex: 0 0 calc(33.33% - 1rem);
  }

  .dashboard-content {
    padding: 1rem 1.5rem;
  }

  .recipe-brief-content {
    flex-direction: column;
  }

  .recipe-brief-image {
    height: 250px;
  }
}

@media (max-width: 992px) {
  .hero-content h1 {
    font-size: 2.5rem;
  }

  .recipe-card {
    flex: 0 0 calc(50% - 0.75rem);
  }

  .newsletter-content {
    flex-direction: column;
    text-align: center;
  }

  .newsletter-form {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 4rem 1.5rem;
  }

  .hero-content h1 {
    font-size: 2rem;
  }

  .hero-content p {
    font-size: 1rem;
  }

  .search-container {
    flex-direction: column;
  }

  .search-container input {
    border-radius: 8px;
    margin-bottom: 0.5rem;
  }

  .search-button {
    border-radius: 8px;
  }

  .categories-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .carousel-btn {
    width: 36px;
    height: 36px;
  }

  .carousel-btn.prev {
    left: -10px;
  }

  .carousel-btn.next {
    right: -10px;
  }

  .newsletter-form {
    flex-direction: column;
  }

  .recipe-brief-actions {
    flex-direction: column;
  }
}

@media (max-width: 576px) {
  .dashboard-content {
    padding: 1rem;
  }

  .hero-section {
    padding: 3rem 1rem;
  }

  .hero-content h1 {
    font-size: 1.8rem;
  }

  .recipe-card {
    flex: 0 0 calc(100% - 1rem);
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .recipe-brief-modal {
    width: 95%;
  }

  .recipe-brief-info {
    padding: 1.5rem;
  }

  .recipe-brief-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>
