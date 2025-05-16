<!-- src/views/AdvancedSearchView.vue -->
<template>
  <div class="advanced-search">
    <div class="search-container">
      <h1>Advanced Recipe Search</h1>

      <div class="search-form">
        <!-- Search Term -->
        <div class="form-group full-width">
          <label for="search">Search Term</label>
          <div class="search-input-container">
            <input
              type="text"
              id="search"
              v-model="searchParams.search"
              placeholder="Enter keywords..."
            />
            <button type="button" class="btn-search" @click="searchRecipes">
              <i class="fas fa-search"></i> Search
            </button>
          </div>
        </div>

        <!-- Ingredients Section
        <div class="ingredients-section">
          <h3>Ingredients</h3>

          <div class="ingredients-container">
            <div class="ingredient-column">
              <label>Include Ingredients</label>
              <div class="ingredient-select">
                <multiselect
                  v-model="searchParams.includeIngredients"
                  :options="availableIngredients"
                  :multiple="true"
                  :close-on-select="false"
                  :clear-on-select="false"
                  :preserve-search="true"
                  placeholder="Select ingredients to include"
                  label="name"
                  track-by="id"
                >
                  <template #tag="{ option, remove }">
                    <span class="multiselect-tag">
                      {{ option.name }}
                      <i @click="remove(option)" class="multiselect-tag-remove">×</i>
                    </span>
                  </template>
                </multiselect>
              </div>
            </div>

            <div class="ingredient-column">
              <label>Exclude Ingredients</label>
              <div class="ingredient-select">
                <multiselect
                  v-model="searchParams.excludeIngredients"
                  :options="availableIngredients"
                  :multiple="true"
                  :close-on-select="false"
                  :clear-on-select="false"
                  :preserve-search="true"
                  placeholder="Select ingredients to exclude"
                  label="name"
                  track-by="id"
                >
                  <template #tag="{ option, remove }">
                    <span class="multiselect-tag exclude">
                      {{ option.name }}
                      <i @click="remove(option)" class="multiselect-tag-remove">×</i>
                    </span>
                  </template>
                </multiselect>
              </div>
            </div>
          </div>
        </div>-->

        <!-- Filter Sections -->
        <div class="filter-sections">
          <!-- Left Column -->
          <div class="filter-column">
            <!-- Time and Difficulty -->
            <div class="filter-section">
              <h3>Time & Difficulty</h3>
              <div class="form-group">
                <label for="maxCookingTime">Max Cooking Time (minutes)</label>
                <div class="slider-container">
                  <input
                    type="range"
                    id="maxCookingTime"
                    v-model="searchParams.maxCookingTime"
                    min="0"
                    max="180"
                    step="5"
                  />
                  <span class="slider-value">{{ searchParams.maxCookingTime || 'No limit' }}</span>
                </div>
              </div>

              <div class="form-group">
                <label>Difficulty Level</label>
                <div class="checkbox-grid">
                  <div v-for="difficulty in difficultyLevels" :key="difficulty" class="checkbox-item">
                    <input
                      type="checkbox"
                      :id="'difficulty-' + difficulty"
                      :value="difficulty"
                      v-model="searchParams.difficulties"
                    />
                    <label :for="'difficulty-' + difficulty">{{ formatEnumValue(difficulty) }}</label>
                  </div>
                </div>
              </div>
            </div>

            <!-- Cuisine Types -->
            <div class="filter-section">
              <h3>Cuisine Types</h3>
              <div class="checkbox-grid">
                <div v-for="cuisine in cuisineTypes" :key="cuisine" class="checkbox-item">
                  <input
                    type="checkbox"
                    :id="'cuisine-' + cuisine"
                    :value="cuisine"
                    v-model="searchParams.cuisines"
                  />
                  <label :for="'cuisine-' + cuisine">{{ formatEnumValue(cuisine) }}</label>
                </div>
              </div>
            </div>
          </div>

          <!-- Middle Column -->
          <div class="filter-column">
            <!-- Meal Types -->
            <div class="filter-section">
              <h3>Meal Types</h3>
              <div class="checkbox-grid">
                <div v-for="mealType in mealTypes" :key="mealType" class="checkbox-item">
                  <input
                    type="checkbox"
                    :id="'mealType-' + mealType"
                    :value="mealType"
                    v-model="searchParams.mealTypes"
                  />
                  <label :for="'mealType-' + mealType">{{ formatEnumValue(mealType) }}</label>
                </div>
              </div>
            </div>

            <!-- Diet Types -->
            <div class="filter-section">
              <h3>Diet Types</h3>
              <div class="checkbox-grid">
                <div v-for="dietType in dietTypes" :key="dietType" class="checkbox-item">
                  <input
                    type="checkbox"
                    :id="'dietType-' + dietType"
                    :value="dietType"
                    v-model="searchParams.dietTypes"
                  />
                  <label :for="'dietType-' + dietType">{{ formatEnumValue(dietType) }}</label>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column -->
          <div class="filter-column">
            <!-- Cooking Methods -->
            <div class="filter-section">
              <h3>Cooking Methods</h3>
              <div class="checkbox-grid">
                <div v-for="method in cookingMethods" :key="method" class="checkbox-item">
                  <input
                    type="checkbox"
                    :id="'method-' + method"
                    :value="method"
                    v-model="searchParams.cookingMethods"
                  />
                  <label :for="'method-' + method">{{ formatEnumValue(method) }}</label>
                </div>
              </div>
            </div>

            <!-- Sort Options -->
            <div class="filter-section">
              <h3>Sort Options</h3>
              <div class="form-row">
                <div class="form-group">
                  <label for="sortBy">Sort By</label>
                  <select id="sortBy" v-model="searchParams.sortBy">
                    <option value="rating">Rating</option>
                    <option value="popularity">Popularity</option>
                    <option value="cookingTime">Cooking Time</option>
                    <option value="name">Name</option>
                    <option value="difficulty">Difficulty</option>
                    <option value="createdAt">Date Added</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="sortDirection">Direction</label>
                  <select id="sortDirection" v-model="searchParams.sortDirection">
                    <option value="asc">Ascending</option>
                    <option value="desc">Descending</option>
                  </select>
                </div>
              </div>

              <div class="form-group">
                <label for="pageSize">Results Per Page</label>
                <select id="pageSize" v-model="searchParams.size" @change="resetPage">
                  <option value="12">12</option>
                  <option value="24">24</option>
                  <option value="36">36</option>
                  <option value="48">48</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="button" class="btn-reset" @click="resetForm">
            <i class="fas fa-undo"></i> Reset Filters
          </button>
          <button type="button" class="btn-search" @click="searchRecipes">
            <i class="fas fa-search"></i> Search Recipes
          </button>
        </div>
      </div>
    </div>

    <div class="search-results">
      <h2>Search Results</h2>

      <div class="results-info" v-if="!loading && recipes.length > 0">
        <p>Found {{ totalElements }} recipes matching your criteria</p>
        <div class="results-sort">
          <label for="mobileSortBy">Sort by:</label>
          <select id="mobileSortBy" v-model="searchParams.sortBy" @change="searchRecipes">
            <option value="rating">Rating</option>
            <option value="popularity">Popularity</option>
            <option value="cookingTime">Cooking Time</option>
            <option value="name">Name</option>
            <option value="difficulty">Difficulty</option>
            <option value="createdAt">Date Added</option>
          </select>
          <select v-model="searchParams.sortDirection" @change="searchRecipes">
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Searching for recipes...</p>
      </div>

      <div v-else-if="recipes.length === 0" class="empty-results">
        <i class="fas fa-search"></i>
        <p>No recipes found matching your criteria</p>
        <button @click="resetForm" class="btn-try-again">Try with different criteria</button>
      </div>

      <div v-else class="results-grid">
        <div class="recipe-card" v-for="recipe in recipes" :key="recipe.id" @click="showRecipeBrief(recipe)">
          <div class="recipe-image">
            <img :src="recipe.imageUrl || '/images/default-recipe.jpg'" :alt="recipe.name" />
            <div class="recipe-time">
              <i class="far fa-clock"></i> {{ recipe.cookingTime }} min
            </div>
            <div class="recipe-difficulty" :class="recipe.difficulty?.toLowerCase()">
              {{ formatEnumValue(recipe.difficulty || 'MEDIUM') }}
            </div>
          </div>
          <div class="recipe-info">
            <div class="recipe-tags">
              <span class="tag-cuisine">{{ formatEnumValue(recipe.cuisine) }}</span>
              <span class="tag-meal">{{ formatEnumValue(recipe.mealType) }}</span>
              <span class="tag-diet" v-if="recipe.dietType">{{ formatEnumValue(recipe.dietType) }}</span>
            </div>
            <h3>{{ recipe.name }}</h3>
            <p class="recipe-description" v-if="recipe.description">{{ truncateText(recipe.description, 80) }}</p>
            <div class="recipe-meta">
              <div class="recipe-rating" v-if="recipe.rating">
                <div class="stars">
                  <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'star-'+i"></i>
                  <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                  <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'empty-'+i"></i>
                </div>
                <span>{{ recipe.rating.toFixed(1) }}</span>
              </div>
              <div class="recipe-popularity" v-if="recipe.popularity">
                <i class="fas fa-fire"></i> {{ recipe.popularity }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination" v-if="recipes.length > 0">
        <button
          class="btn-page"
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          <i class="fas fa-chevron-left"></i> Previous
        </button>

        <div class="page-numbers">
          <button
            v-for="page in displayedPages"
            :key="page"
            class="btn-page-number"
            :class="{ active: page === currentPage + 1, ellipsis: page === '...' }"
            @click="page !== '...' && changePage(page - 1)"
            :disabled="page === '...'"
          >
            {{ page }}
          </button>
        </div>

        <button
          class="btn-page"
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)"
        >
          Next <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- Recipe Brief Modal -->
    <div class="modal-overlay" v-if="showModal" @click="closeModal">
      <div class="recipe-brief-modal" @click.stop>
        <button class="close-modal" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>

        <div v-if="selectedRecipe && !loadingRecipeBrief" class="recipe-brief-content">
          <div class="recipe-brief-image">
            <img :src="recipeBrief.imageUrl || '/images/default-recipe.jpg'" :alt="recipeBrief.name" />
            <div class="recipe-brief-difficulty" :class="recipeBrief.difficulty?.toLowerCase()">
              {{ formatEnumValue(recipeBrief.difficulty || '') }}
            </div>
          </div>
          <div class="recipe-brief-info">
            <h2>{{ recipeBrief.name }}</h2>

            <div class="recipe-brief-meta">
              <div class="recipe-brief-tags">
                <span class="tag-cuisine">{{ formatEnumValue(recipeBrief.cuisine || '') }}</span>
                <span class="tag-meal">{{ formatEnumValue(recipeBrief.mealType || '') }}</span>
                <span class="tag-diet" v-if="recipeBrief.dietType">{{ formatEnumValue(recipeBrief.dietType) }}</span>
                <span class="tag-method" v-if="recipeBrief.cookingMethod">{{ formatEnumValue(recipeBrief.cookingMethod) }}</span>
              </div>
              <div class="recipe-brief-time">
                <i class="far fa-clock"></i> {{ recipeBrief.cookingTime }} min
              </div>
            </div>

            <div class="recipe-brief-rating">
              <div class="stars">
                <i class="fas fa-star" v-for="i in Math.floor(recipeBrief.rating || 0)" :key="'modal-star-'+i"></i>
                <i class="fas fa-star-half-alt" v-if="(recipeBrief.rating || 0) % 1 >= 0.5"></i>
                <i class="far fa-star" v-for="i in (5 - Math.ceil(recipeBrief.rating || 0))" :key="'modal-empty-'+i"></i>
              </div>
              <span>{{ (recipeBrief.rating || 0).toFixed(1) }}</span>

              <div class="recipe-brief-popularity" v-if="recipeBrief.popularity">
                <i class="fas fa-fire"></i> {{ recipeBrief.popularity }} popularity
              </div>
            </div>

            <p class="recipe-brief-description">{{ recipeBrief.description }}</p>

            <div class="recipe-brief-ingredients">
              <h3>Main Ingredients</h3>
              <ul>
                <li v-for="(ingredient, index) in recipeBrief.ingredients?.slice(0, 5)" :key="index">
                  {{ ingredient.ingredientName }} - {{ ingredient.quantity }} {{ ingredient.unit }}
                </li>
                <li v-if="recipeBrief.ingredients?.length > 5" class="more-ingredients">
                  And {{ recipeBrief.ingredients.length - 5 }} more ingredients...
                </li>
              </ul>
            </div>

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

    <!-- Toast Notifications -->
    <div class="toast-container" v-if="showToast">
      <div class="toast" :class="toastType">
        <div class="toast-content">
          <i class="fas" :class="toastIcon"></i>
          <span>{{ toastMessage }}</span>
        </div>
        <button class="toast-close" @click="closeToast">×</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  advancedSearchRecipes,
  getRecipeBrief,
  favoriteRecipe,
  unfavoriteRecipe,
  getAllIngredients
} from '@/services/api';
import Multiselect from 'vue-multiselect';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// Enum values
const cuisineTypes = ['ITALIAN', 'FRENCH', 'CHINESE', 'INDIAN', 'MEXICAN', 'JAPANESE', 'THAI', 'MEDITERRANEAN', 'AMERICAN', 'MIDDLE_EASTERN'];
const mealTypes = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'DESSERT', 'APPETIZER'];
const dietTypes = ['VEGETARIAN', 'VEGAN', 'GLUTEN_FREE', 'DAIRY_FREE', 'KETO', 'PALEO', 'LOW_CARB', 'LOW_FAT'];
const cookingMethods = ['BAKING', 'GRILLING', 'FRYING', 'BOILING', 'STEAMING', 'ROASTING', 'SLOW_COOKING', 'PRESSURE_COOKING'];
const difficultyLevels = ['EASY', 'MEDIUM', 'HARD'];

// Available ingredients for selection
const availableIngredients = ref([]);

// Search parameters
const searchParams = reactive({
  search: '',
  page: 0,
  size: 12,
  includeIngredients: [],
  excludeIngredients: [],
  maxCookingTime: null,
  cuisines: [],
  mealTypes: [],
  dietTypes: [],
  cookingMethods: [],
  difficulties: [],
  sortBy: 'rating',
  sortDirection: 'desc'
});

// Results state
const recipes = ref([]);
const loading = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);

// Modal state
const showModal = ref(false);
const selectedRecipe = ref(null);
const recipeBrief = ref({});
const loadingRecipeBrief = ref(false);
const isFavorite = ref(false);

// Toast notification state
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('info');
const toastTimeout = ref(null);

// Check if user is authenticated
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Toast icon based on type
const toastIcon = computed(() => {
  switch (toastType.value) {
    case 'success': return 'fa-check-circle';
    case 'error': return 'fa-exclamation-circle';
    case 'warning': return 'fa-exclamation-triangle';
    default: return 'fa-info-circle';
  }
});

// Calculate displayed page numbers
const displayedPages = computed(() => {
  const pages = [];
  const maxVisiblePages = 5;

  if (totalPages.value <= maxVisiblePages) {
    // Show all pages if there are fewer than maxVisiblePages
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    // Always include first page
    pages.push(1);

    // Calculate start and end of visible pages
    let start = Math.max(2, currentPage.value - 1);
    let end = Math.min(totalPages.value - 1, start + maxVisiblePages - 3);

    // Adjust start if end is too close to totalPages
    if (end === totalPages.value - 1) {
      start = Math.max(2, end - (maxVisiblePages - 3));
    }

    // Add ellipsis if needed
    if (start > 2) {
      pages.push('...');
    }

    // Add visible pages
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    // Add ellipsis if needed
    if (end < totalPages.value - 1) {
      pages.push('...');
    }

    // Always include last page
    pages.push(totalPages.value);
  }

  return pages;
});

// Format enum values for display
const formatEnumValue = (value) => {
  if (!value) return '';
  return value
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, char => char.toUpperCase());
};

// Truncate text
const truncateText = (text, maxLength) => {
  if (!text) return '';
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};

// Fetch ingredients for selection
const fetchIngredients = async () => {
  try {
    const response = await getAllIngredients(0, 1000); // Get all ingredients
    availableIngredients.value = response.data.content;
  } catch (error) {
    console.error('Error fetching ingredients:', error);
    showToastNotification('Failed to load ingredients', 'error');
  }
};

// Search recipes
const searchRecipes = async () => {
  loading.value = true;

  try {
    // Convert ingredient objects to IDs
    const includeIds = searchParams.includeIngredients.map(ing => ing.id);
    const excludeIds = searchParams.excludeIngredients.map(ing => ing.id);

    // Create a params object with properly formatted arrays
    const params = {
      search: searchParams.search,
      page: currentPage.value,
      size: searchParams.size,
      sortBy: searchParams.sortBy,
      sortDirection: searchParams.sortDirection
    };

    // Add maxCookingTime if it exists
    if (searchParams.maxCookingTime) {
      params.maxCookingTime = searchParams.maxCookingTime;
    }

    // Format array parameters as comma-separated strings
    if (includeIds.length > 0) {
      params.includeIngredients = includeIds.join(',');
    }

    if (excludeIds.length > 0) {
      params.excludeIngredients = excludeIds.join(',');
    }

    if (searchParams.cuisines.length > 0) {
      params.cuisines = searchParams.cuisines.join(',');
    }

    if (searchParams.mealTypes.length > 0) {
      params.mealTypes = searchParams.mealTypes.join(',');
    }

    if (searchParams.dietTypes.length > 0) {
      params.dietTypes = searchParams.dietTypes.join(',');
    }

    if (searchParams.cookingMethods.length > 0) {
      params.cookingMethods = searchParams.cookingMethods.join(',');
    }

    if (searchParams.difficulties.length > 0) {
      params.difficulties = searchParams.difficulties.join(',');
    }

    // Make the API call with the formatted params
    const response = await advancedSearchRecipes(params);

    recipes.value = response.data.content;
    totalPages.value = response.data.totalPages;
    totalElements.value = response.data.totalElements;
    currentPage.value = response.data.pageNumber;

    // Update URL with search parameters
    updateUrlParams();

    if (recipes.value.length === 0 && totalElements.value === 0) {
      showToastNotification('No recipes found matching your criteria', 'info');
    }

  } catch (error) {
    console.error('Error searching recipes:', error);
    showToastNotification('Error searching recipes', 'error');
  } finally {
    loading.value = false;
  }
};

// Change page
const changePage = (page) => {
  currentPage.value = page;
  searchRecipes();
};

// Reset page when changing page size
const resetPage = () => {
  currentPage.value = 0;
  searchRecipes();
};

// Reset form
const resetForm = () => {
  Object.assign(searchParams, {
    search: '',
    page: 0,
    size: 12,
    includeIngredients: [],
    excludeIngredients: [],
    maxCookingTime: null,
    cuisines: [],
    mealTypes: [],
    dietTypes: [],
    cookingMethods: [],
    difficulties: [],
    sortBy: 'rating',
    sortDirection: 'desc'
  });

  currentPage.value = 0;

  // Clear URL parameters
  router.replace({ query: {} });

  // Perform search with reset filters
  searchRecipes();
};

// Update URL parameters
const updateUrlParams = () => {
  const query = {};

  if (searchParams.search) query.search = searchParams.search;
  if (searchParams.maxCookingTime) query.maxCookingTime = searchParams.maxCookingTime;

  if (searchParams.includeIngredients.length) {
    query.includeIngredients = searchParams.includeIngredients.map(ing => ing.id).join(',');
  }

  if (searchParams.excludeIngredients.length) {
    query.excludeIngredients = searchParams.excludeIngredients.map(ing => ing.id).join(',');
  }

  if (searchParams.cuisines.length) query.cuisines = searchParams.cuisines.join(',');
  if (searchParams.mealTypes.length) query.mealTypes = searchParams.mealTypes.join(',');
  if (searchParams.dietTypes.length) query.dietTypes = searchParams.dietTypes.join(',');
  if (searchParams.cookingMethods.length) query.cookingMethods = searchParams.cookingMethods.join(',');
  if (searchParams.difficulties.length) query.difficulties = searchParams.difficulties.join(',');

  if (searchParams.sortBy !== 'rating') query.sortBy = searchParams.sortBy;
  if (searchParams.sortDirection !== 'desc') query.sortDirection = searchParams.sortDirection;
  if (searchParams.size !== 12) query.size = searchParams.size;
  if (currentPage.value > 0) query.page = currentPage.value;

  router.replace({ query });
};

// Show recipe brief modal
const showRecipeBrief = async (recipe) => {
  selectedRecipe.value = recipe;
  showModal.value = true;
  loadingRecipeBrief.value = true;

  try {
    const response = await getRecipeBrief(recipe.id);
    recipeBrief.value = response.data;

    // Check if recipe is favorited
    isFavorite.value = recipeBrief.value.isFavorite || false;
  } catch (error) {
    console.error('Error fetching recipe brief:', error);
    showToastNotification('Failed to load recipe details', 'error');
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
      showToastNotification('Recipe removed from favorites', 'success');
    } else {
      await favoriteRecipe(selectedRecipe.value.id);
      isFavorite.value = true;
      showToastNotification('Recipe added to favorites', 'success');
    }
  } catch (error) {
    console.error('Error toggling favorite:', error);
    showToastNotification('Failed to update favorites', 'error');
  }
};

// Show toast notification
const showToastNotification = (message, type = 'info') => {
  // Clear any existing timeout
  if (toastTimeout.value) {
    clearTimeout(toastTimeout.value);
  }

  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;

  // Auto-hide after 3 seconds
  toastTimeout.value = setTimeout(() => {
    closeToast();
  }, 3000);
};

// Close toast notification
const closeToast = () => {
  showToast.value = false;
  if (toastTimeout.value) {
    clearTimeout(toastTimeout.value);
  }
};

// Initialize from URL parameters
const initFromUrlParams = () => {
  // Parse query parameters
  if (route.query.search) searchParams.search = route.query.search;
  if (route.query.maxCookingTime) searchParams.maxCookingTime = Number(route.query.maxCookingTime);

  if (route.query.includeIngredients) {
    const includeIdsStr = route.query.includeIngredients;
    const includeIds = includeIdsStr.includes(',')
      ? includeIdsStr.split(',')
      : [includeIdsStr];

    // We'll need to map these IDs to actual ingredient objects once ingredients are loaded
    searchParams.includeIngredients = includeIds.map(id => ({ id }));
  }

  if (route.query.excludeIngredients) {
    const excludeIdsStr = route.query.excludeIngredients;
    const excludeIds = excludeIdsStr.includes(',')
      ? excludeIdsStr.split(',')
      : [excludeIdsStr];

    searchParams.excludeIngredients = excludeIds.map(id => ({ id }));
  }

  if (route.query.cuisines) {
    const cuisinesStr = route.query.cuisines;
    searchParams.cuisines = cuisinesStr.includes(',')
      ? cuisinesStr.split(',')
      : [cuisinesStr];
  }

  if (route.query.mealTypes) {
    const mealTypesStr = route.query.mealTypes;
    searchParams.mealTypes = mealTypesStr.includes(',')
      ? mealTypesStr.split(',')
      : [mealTypesStr];
  }

  if (route.query.dietTypes) {
    const dietTypesStr = route.query.dietTypes;
    searchParams.dietTypes = dietTypesStr.includes(',')
      ? dietTypesStr.split(',')
      : [dietTypesStr];
  }

  if (route.query.cookingMethods) {
    const cookingMethodsStr = route.query.cookingMethods;
    searchParams.cookingMethods = cookingMethodsStr.includes(',')
      ? cookingMethodsStr.split(',')
      : [cookingMethodsStr];
  }

  if (route.query.difficulties) {
    const difficultiesStr = route.query.difficulties;
    searchParams.difficulties = difficultiesStr.includes(',')
      ? difficultiesStr.split(',')
      : [difficultiesStr];
  }

  if (route.query.sortBy) searchParams.sortBy = route.query.sortBy;
  if (route.query.sortDirection) searchParams.sortDirection = route.query.sortDirection;
  if (route.query.size) searchParams.size = Number(route.query.size);
  if (route.query.page) currentPage.value = Number(route.query.page);
};

// Update ingredient objects once ingredients are loaded
const updateIngredientObjects = () => {
  if (availableIngredients.value.length > 0) {
    // Update include ingredients
    if (searchParams.includeIngredients.length > 0) {
      searchParams.includeIngredients = searchParams.includeIngredients
        .map(item => {
          const ingredient = availableIngredients.value.find(ing => ing.id === item.id);
          return ingredient || null;
        })
        .filter(Boolean);
    }

    // Update exclude ingredients
    if (searchParams.excludeIngredients.length > 0) {
      searchParams.excludeIngredients = searchParams.excludeIngredients
        .map(item => {
          const ingredient = availableIngredients.value.find(ing => ing.id === item.id);
          return ingredient || null;
        })
        .filter(Boolean);
    }
  }
};

// Initialize
onMounted(async () => {
  // First load ingredients
  await fetchIngredients();

  // Then initialize from URL parameters
  initFromUrlParams();

  // Update ingredient objects with full data
  updateIngredientObjects();

  // Perform initial search
  searchRecipes();
});

// Watch for changes in available ingredients to update selected ingredients
watch(availableIngredients, () => {
  updateIngredientObjects();
});
</script>

<style>
/* Import vue-multiselect styles */
@import 'vue-multiselect/dist/vue-multiselect.css';
</style>

<style scoped>
.advanced-search {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;
}

h1 {
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.search-container {
  background-color: var(--card-bg);
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 15px var(--shadow-color);
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.full-width {
  width: 100%;
}

.search-input-container {
  display: flex;
  gap: 0.5rem;
}

.search-input-container input {
  flex: 1;
}

/* Ingredients Section */
.ingredients-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.ingredients-section h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.ingredients-container {
  display: flex;
  gap: 1.5rem;
}

.ingredient-column {
  flex: 1;
}

.ingredient-column label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

.ingredient-select {
  width: 100%;
}

/* Customize multiselect */
:deep(.multiselect) {
  border-radius: 8px;
  border-color: var(--border-color);
}

:deep(.multiselect__tags) {
  border-radius: 8px;
  padding: 0.6rem;
  min-height: 42px;
  background-color: var(--bg-color);
}

:deep(.multiselect__tag) {
  background-color: var(--primary-color);
  color: white;
  border-radius: 4px;
}

:deep(.multiselect__tag.exclude) {
  background-color: #f44336;
}

:deep(.multiselect__tag-icon:after) {
  color: white;
}

:deep(.multiselect__tag-icon:hover) {
  background-color: rgba(0, 0, 0, 0.2);
}

:deep(.multiselect__input) {
  background-color: var(--bg-color);
  color: var(--text-color);
}

:deep(.multiselect__placeholder) {
  color: var(--text-light);
  padding-top: 0;
}

:deep(.multiselect__content-wrapper) {
  border-color: var(--border-color);
  background-color: var(--bg-color);
}

:deep(.multiselect__option--highlight) {
  background-color: var(--primary-color);
}

/* Filter Sections */
.filter-sections {
  display: flex;
  gap: 2rem;
}

.filter-column {
  flex: 1;
  min-width: 0; /* Prevent overflow */
}

.filter-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.filter-section:last-child {
  border-bottom: none;
  padding-bottom: 0;
  margin-bottom: 0;
}

.filter-section h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-group {
  margin-bottom: 1rem;
  flex: 1;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.form-group input[type="text"]:focus,
.form-group input[type="number"]:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary-color);
}

/* Slider styles */
.slider-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.slider-container input[type="range"] {
  flex: 1;
  -webkit-appearance: none;
  width: 100%;
  height: 6px;
  border-radius: 5px;
  background: var(--border-color);
  outline: none;
}

.slider-container input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--primary-color);
  cursor: pointer;
  transition: all 0.2s;
}

.slider-container input[type="range"]::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.slider-container input[type="range"]::-moz-range-thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--primary-color);
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.slider-container input[type="range"]::-moz-range-thumb:hover {
  transform: scale(1.2);
}

.slider-value {
  min-width: 60px;
  text-align: right;
  color: var(--text-color);
  font-weight: 500;
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 0.5rem;
  max-height: 200px;
  overflow-y: auto;
  padding-right: 0.5rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
}

.checkbox-item input[type="checkbox"] {
  margin-right: 0.5rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
  border-top: 1px solid var(--border-color);
  padding-top: 1.5rem;
}

.btn-reset, .btn-search {
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
}

.btn-reset {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.btn-reset:hover {
  background-color: var(--border-color);
}

.btn-search {
  background-color: var(--primary-color);
  color: white;
}

.btn-search:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* Search Results */
.search-results {
  margin-top: 2rem;
}

.search-results h2 {
  margin-bottom: 1rem;
  color: var(--text-color);
}

.results-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.results-info p {
  margin: 0;
  color: var(--text-light);
}

.results-sort {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.results-sort label {
  color: var(--text-light);
}

.results-sort select {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
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

.empty-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
  text-align: center;
  color: var(--text-light);
}

.empty-results i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.btn-try-again {
  margin-top: 1rem;
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  background-color: var(--primary-color);
  border: none;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-try-again:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.recipe-card {
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

.recipe-difficulty {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
}

.recipe-difficulty.easy {
  background-color: #4CAF50;
}

.recipe-difficulty.medium {
  background-color: #FF9800;
}

.recipe-difficulty.hard {
  background-color: #F44336;
}

.recipe-info {
  padding: 1rem;
}

.recipe-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.recipe-tags span {
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}

.tag-cuisine {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
}

.tag-meal {
  background-color: rgba(33, 150, 243, 0.1);
  color: #2196F3;
}

.tag-diet {
  background-color: rgba(156, 39, 176, 0.1);
  color: #9C27B0;
}

.tag-method {
  background-color: rgba(255, 87, 34, 0.1);
  color: #FF5722;
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

.recipe-description {
  font-size: 0.9rem;
  color: var(--text-light);
  margin-bottom: 0.8rem;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recipe-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.recipe-popularity {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: #FF5722;
  font-size: 0.9rem;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 0.5rem;
}

.btn-page {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-page:hover:not(:disabled) {
  background-color: var(--primary-color);
  color: white;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.3rem;
}

.btn-page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-page-number.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.btn-page-number:hover:not(.active):not(.ellipsis) {
  background-color: var(--border-color);
}

.btn-page-number.ellipsis {
  cursor: default;
  border: none;
  background: transparent;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
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
  position: relative;
}

.recipe-brief-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recipe-brief-difficulty {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
}

.recipe-brief-difficulty.easy {
  background-color: #4CAF50;
}

.recipe-brief-difficulty.medium {
  background-color: #FF9800;
}

.recipe-brief-difficulty.hard {
  background-color: #F44336;
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
  flex-wrap: wrap;
  gap: 0.5rem;
}

.recipe-brief-tags span {
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.recipe-brief-time {
  color: var(--text-light);
  font-size: 0.9rem;
}

.recipe-brief-rating {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.recipe-brief-popularity {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #FF5722;
}

.recipe-brief-description {
  margin-bottom: 1.5rem;
  line-height: 1.6;
  color: var(--text-color);
}

.recipe-brief-ingredients {
  margin-bottom: 1.5rem;
}

.recipe-brief-ingredients h3 {
  font-size: 1.2rem;
  margin-bottom: 0.8rem;
  color: var(--text-color);
}

.recipe-brief-ingredients ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.recipe-brief-ingredients li {
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-color);
}

.recipe-brief-ingredients li:last-child {
  border-bottom: none;
}

.recipe-brief-ingredients .more-ingredients {
  font-style: italic;
  color: var(--text-light);
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

.recipe-brief-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

/* Toast Notifications */
.toast-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1100;
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from { transform: translateX(100%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.toast {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-bottom: 10px;
  min-width: 300px;
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.toast.success {
  background-color: #4CAF50;
  color: white;
}

.toast.error {
  background-color: #F44336;
  color: white;
}

.toast.warning {
  background-color: #FF9800;
  color: white;
}

.toast.info {
  background-color: #2196F3;
  color: white;
}

.toast-close {
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.toast-close:hover {
  opacity: 1;
}

/* Responsive styles */
@media (max-width: 992px) {
  .filter-sections {
    flex-direction: column;
    gap: 1rem;
  }

  .filter-column {
    width: 100%;
  }

  .ingredients-container {
    flex-direction: column;
  }

  .checkbox-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}

@media (max-width: 768px) {
  .advanced-search {
    padding: 1rem;
  }

  .search-container {
    padding: 1.5rem;
  }

  .search-input-container {
    flex-direction: column;
  }

  .form-row {
    flex-direction: column;
  }

  .checkbox-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    max-height: 150px;
  }

  .recipe-brief-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .recipe-brief-actions {
    flex-direction: column;
  }

  .results-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}

@media (max-width: 576px) {
  .form-actions {
    flex-direction: column;
  }

  .btn-reset, .btn-search {
    width: 100%;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }

  .page-numbers {
    order: 3;
    margin-top: 0.5rem;
    width: 100%;
    justify-content: center;
  }

  .toast-container {
    left: 20px;
    right: 20px;
  }

  .toast {
    min-width: auto;
    width: 100%;
  }
}
</style>
