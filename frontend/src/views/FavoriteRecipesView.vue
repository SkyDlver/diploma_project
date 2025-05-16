<!-- src/views/FavoriteRecipesView.vue -->
<template>
  <div class="favorite-recipes-page">
    <div class="page-header">
      <h1>My Favorite Recipes</h1>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading your favorite recipes...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="recipes.length === 0" class="empty-state">
      <i class="fas fa-heart-broken"></i>
      <h2>No Favorite Recipes Yet</h2>
      <p>Explore recipes and click the heart icon to add them to your favorites.</p>
      <router-link to="/recipes" class="btn-primary">
        <i class="fas fa-search"></i> Explore Recipes
      </router-link>
    </div>

    <!-- Recipes Grid -->
    <div v-else class="recipes-grid">
      <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
        <div class="recipe-image">
          <img :src="recipe.imageUrl || '/images/default-recipe.jpg'" :alt="recipe.name">
          <button class="favorite-btn active" @click="unfavoriteRecipe(recipe)">
            <i class="fas fa-heart"></i>
          </button>
        </div>
        <div class="recipe-content">
          <router-link :to="`/recipe/${recipe.id}`" class="recipe-title">
            {{ recipe.name }}
          </router-link>
          <div class="recipe-meta">
            <span v-if="recipe.cuisine" class="meta-item">
              <i class="fas fa-globe-americas"></i> {{ formatEnum(recipe.cuisine) }}
            </span>
            <span v-if="recipe.mealType" class="meta-item">
              <i class="fas fa-utensils"></i> {{ formatEnum(recipe.mealType) }}
            </span>
            <span class="meta-item">
              <i class="fas fa-clock"></i> {{ recipe.cookingTime }} min
            </span>
          </div>
          <p class="recipe-description">{{ truncateText(recipe.description, 100) }}</p>
          <div class="recipe-footer">
            <div class="recipe-rating">
              <i class="fas fa-star"></i>
              <span>{{ recipe.rating ? recipe.rating.toFixed(1) : 'N/A' }}</span>
            </div>
            <div class="recipe-difficulty">
              {{ formatEnum(recipe.difficulty) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && totalPages > 1" class="pagination">
      <button
        class="pagination-btn"
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        <i class="fas fa-chevron-left"></i> Previous
      </button>

      <div class="page-info">
        Page {{ currentPage + 1 }} of {{ totalPages }}
      </div>

      <button
        class="pagination-btn"
        :disabled="currentPage >= totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        Next <i class="fas fa-chevron-right"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getUserFavoriteRecipes, unfavoriteRecipe as apiUnfavoriteRecipe } from '@/services/api';

// State
const recipes = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 12;

// Fetch favorite recipes
const fetchFavorites = async (page = 0) => {
  loading.value = true;
  try {
    const response = await getUserFavoriteRecipes(page, pageSize);
    recipes.value = response.data.content;
    totalPages.value = response.data.totalPages;
    currentPage.value = response.data.number;
  } catch (error) {
    console.error('Error fetching favorite recipes:', error);
  } finally {
    loading.value = false;
  }
};

// Change page
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    fetchFavorites(page);
  }
};

// Remove recipe from favorites
const unfavoriteRecipe = async (recipe) => {
  try {
    await apiUnfavoriteRecipe(recipe.id);
    // Remove from local state
    recipes.value = recipes.value.filter(r => r.id !== recipe.id);

    // If we removed the last recipe on the page and there are more pages,
    // fetch the previous page
    if (recipes.value.length === 0 && currentPage.value > 0) {
      fetchFavorites(currentPage.value - 1);
    }
  } catch (error) {
    console.error('Error removing recipe from favorites:', error);
  }
};

// Format enum values
const formatEnum = (value) => {
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

// Initialize
onMounted(() => {
  fetchFavorites();
});
</script>

<style scoped>
.favorite-recipes-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  color: var(--text-color);
}

/* Loading and Empty States */
.loading-container, .empty-state {
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

.empty-state i {
  font-size: 4rem;
  color: #e0e0e0;
  margin-bottom: 1rem;
}

.empty-state h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.empty-state p {
  margin-bottom: 2rem;
  color: var(--text-light);
  max-width: 500px;
}

/* Recipes Grid */
.recipes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.recipe-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: var(--card-bg);
}

.recipe-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px var(--shadow-color);
}

.recipe-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.recipe-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.recipe-card:hover .recipe-image img {
  transform: scale(1.05);
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.favorite-btn i {
  font-size: 1.1rem;
  color: #ccc;
}

.favorite-btn.active i {
  color: #ff5252;
}

.favorite-btn:hover {
  transform: scale(1.1);
}

.recipe-content {
  padding: 1.5rem;
}

.recipe-title {
  display: block;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 0.8rem;
  text-decoration: none;
}

.recipe-title:hover {
  color: var(--primary-color);
}

.recipe-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 0.8rem;
  font-size: 0.85rem;
  color: var(--text-light);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.recipe-description {
  color: var(--text-color);
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recipe-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.8rem;
  border-top: 1px solid var(--border-color);
}

.recipe-rating {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: var(--text-color);
}

.recipe-rating i {
  color: #ffc107;
}

.recipe-difficulty {
  font-size: 0.85rem;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 1rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  background-color: var(--border-color);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-light);
  font-size: 0.9rem;
}

/* Button Styles */
.btn-primary {
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
}

.btn-primary:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

/* Responsive Styles */
@media (max-width: 768px) {
  .favorite-recipes-page {
    padding: 1rem;
  }

  .recipes-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .recipes-grid {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-direction: column;
    gap: 0.5rem;
  }

  .pagination-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
