<!-- src/views/MyRecipesView.vue -->
<template>
  <div class="my-recipes-page">
    <div class="page-header">
      <h1>My Recipes</h1>
      <button class="btn-primary" @click="openCreateModal">
        <i class="fas fa-plus"></i> Create New Recipe
      </button>
    </div>

    <!-- Filter and Sort Bar -->
    <div class="filter-sort-bar">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search my recipes..."
          @input="debounceSearch"
        />
        <i class="fas fa-search search-icon"></i>
      </div>

      <div class="filter-options">
        <div class="filter-select">
          <label for="cuisine-filter">Cuisine:</label>
          <select id="cuisine-filter" v-model="filters.cuisine" @change="filterRecipes">
            <option value="">All Cuisines</option>
            <option v-for="cuisine in cuisineTypes" :key="cuisine" :value="cuisine">
              {{ formatEnumValue(cuisine) }}
            </option>
          </select>
        </div>

        <div class="filter-select">
          <label for="meal-filter">Meal Type:</label>
          <select id="meal-filter" v-model="filters.mealType" @change="filterRecipes">
            <option value="">All Meal Types</option>
            <option v-for="mealType in mealTypes" :key="mealType" :value="mealType">
              {{ formatEnumValue(mealType) }}
            </option>
          </select>
        </div>

        <div class="filter-select">
          <label for="sort-by">Sort By:</label>
          <select id="sort-by" v-model="sortOption" @change="filterRecipes">
            <option value="newest">Newest First</option>
            <option value="oldest">Oldest First</option>
            <option value="name_asc">Name (A-Z)</option>
            <option value="name_desc">Name (Z-A)</option>
            <option value="rating">Highest Rating</option>
            <option value="popularity">Most Popular</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Recipes Grid -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading your recipes...</p>
    </div>

    <div v-else-if="recipes.length === 0" class="empty-state">
      <i class="fas fa-utensils"></i>
      <p v-if="searchQuery || filters.cuisine || filters.mealType">No recipes found matching your criteria</p>
      <p v-else>You haven't created any recipes yet</p>
      <button v-if="searchQuery || filters.cuisine || filters.mealType" @click="resetFilters" class="btn-secondary">
        Clear Filters
      </button>
      <button v-else @click="openCreateModal" class="btn-primary">
        Create Your First Recipe
      </button>
    </div>

    <div v-else class="recipes-grid">
      <div
        v-for="recipe in recipes"
        :key="recipe.id"
        class="recipe-card"
      >
        <div class="recipe-image" @click="viewRecipeDetails(recipe.id)">
          <img :src="recipe.imageUrl || '/images/default-recipe.jpg'" :alt="recipe.name" />
          <div class="recipe-time">
            <i class="far fa-clock"></i> {{ recipe.cookingTime }} min
          </div>
          <div class="recipe-difficulty" :class="recipe.difficulty?.toLowerCase()">
            {{ formatEnumValue(recipe.difficulty || 'MEDIUM') }}
          </div>
        </div>
        <div class="recipe-info">
          <h3 @click="viewRecipeDetails(recipe.id)">{{ recipe.name }}</h3>
          <div class="recipe-tags">
            <span class="tag-cuisine">{{ formatEnumValue(recipe.cuisine) }}</span>
            <span class="tag-meal">{{ formatEnumValue(recipe.mealType) }}</span>
            <span class="tag-diet" v-if="recipe.dietType">{{ formatEnumValue(recipe.dietType) }}</span>
          </div>
          <p class="recipe-description">{{ truncateText(recipe.description || 'No description available', 100) }}</p>
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
          <div class="recipe-actions">
            <button class="btn-icon" @click="openEditModal(recipe)">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn-icon delete" @click="confirmDelete(recipe)">
              <i class="fas fa-trash-alt"></i>
            </button>
            <button class="btn-icon view" @click="viewRecipeDetails(recipe.id)">
              <i class="fas fa-eye"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination" v-if="totalPages > 1">
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

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" v-if="showDeleteModal" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <h2>Delete Recipe</h2>
        <p>Are you sure you want to delete <strong>{{ recipeToDelete?.name }}</strong>?</p>
        <p class="warning">This action cannot be undone.</p>

        <div class="modal-actions">
          <button class="btn-secondary" @click="cancelDelete">Cancel</button>
          <button class="btn-danger" @click="deleteRecipe">Delete</button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Recipe Modal -->
    <div class="modal-overlay" v-if="showRecipeFormModal">
      <div class="modal-content recipe-form-modal" @click.stop>
        <button class="close-modal" @click="closeRecipeFormModal">
          <i class="fas fa-times"></i>
        </button>

        <div class="recipe-form">
          <h2>{{ isEditing ? 'Edit Recipe' : 'Create New Recipe' }}</h2>

          <form @submit.prevent="submitRecipeForm">
            <!-- Basic Info Section -->
            <div class="form-section">
              <h3>Basic Information</h3>

              <div class="form-group">
                <label for="recipe-name">Recipe Name *</label>
                <input
                  type="text"
                  id="recipe-name"
                  v-model="recipeForm.name"
                  required
                  :class="{ 'error': formErrors.name }"
                />
                <span class="error-message" v-if="formErrors.name">{{ formErrors.name }}</span>
              </div>

              <div class="form-group">
                <label for="recipe-description">Description</label>
                <textarea
                  id="recipe-description"
                  v-model="recipeForm.description"
                  rows="3"
                  placeholder="Brief description of your recipe"
                ></textarea>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label for="recipe-cuisine">Cuisine Type *</label>
                  <select
                    id="recipe-cuisine"
                    v-model="recipeForm.cuisine"
                    required
                    :class="{ 'error': formErrors.cuisine }"
                  >
                    <option value="" disabled>Select cuisine</option>
                    <option v-for="cuisine in cuisineTypes" :key="cuisine" :value="cuisine">
                      {{ formatEnumValue(cuisine) }}
                    </option>
                  </select>
                  <span class="error-message" v-if="formErrors.cuisine">{{ formErrors.cuisine }}</span>
                </div>

                <div class="form-group">
                  <label for="recipe-meal-type">Meal Type *</label>
                  <select
                    id="recipe-meal-type"
                    v-model="recipeForm.mealType"
                    required
                    :class="{ 'error': formErrors.mealType }"
                  >
                    <option value="" disabled>Select meal type</option>
                    <option v-for="mealType in mealTypes" :key="mealType" :value="mealType">
                      {{ formatEnumValue(mealType) }}
                    </option>
                  </select>
                  <span class="error-message" v-if="formErrors.mealType">{{ formErrors.mealType }}</span>
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label for="recipe-diet">Diet Type</label>
                  <select id="recipe-diet" v-model="recipeForm.dietType">
                    <option value="">None</option>
                    <option v-for="dietType in dietTypes" :key="dietType" :value="dietType">
                      {{ formatEnumValue(dietType) }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="recipe-cooking-method">Cooking Method</label>
                  <select id="recipe-cooking-method" v-model="recipeForm.cookingMethod">
                    <option value="">None</option>
                    <option v-for="method in cookingMethods" :key="method" :value="method">
                      {{ formatEnumValue(method) }}
                    </option>
                  </select>
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label for="recipe-cooking-time">Cooking Time (minutes) *</label>
                  <input
                    type="number"
                    id="recipe-cooking-time"
                    v-model="recipeForm.cookingTime"
                    min="1"
                    required
                    :class="{ 'error': formErrors.cookingTime }"
                  />
                  <span class="error-message" v-if="formErrors.cookingTime">{{ formErrors.cookingTime }}</span>
                </div>

                <div class="form-group">
                  <label for="recipe-difficulty">Difficulty Level</label>
                  <select id="recipe-difficulty" v-model="recipeForm.difficulty">
                    <option value="">Select difficulty</option>
                    <option v-for="level in difficultyLevels" :key="level" :value="level">
                      {{ formatEnumValue(level) }}
                    </option>
                  </select>
                </div>
              </div>

              <div class="form-group">
                <label for="recipe-image">Image URL</label>
                <input
                  type="text"
                  id="recipe-image"
                  v-model="recipeForm.imageUrl"
                  placeholder="https://example.com/image.jpg"
                />
                <div class="image-preview" v-if="recipeForm.imageUrl">
                  <img :src="recipeForm.imageUrl" alt="Recipe preview" />
                </div>
              </div>
            </div>

            <!-- Ingredients Section -->
            <div class="form-section">
              <h3>Ingredients</h3>
              <p class="section-description">Add all ingredients needed for your recipe</p>

              <div
                v-for="(ingredient, index) in recipeForm.ingredients"
                :key="index"
                class="ingredient-row"
              >
                <div class="ingredient-select">
                  <label :for="`ingredient-${index}`">Ingredient *</label>
                  <select
                    :id="`ingredient-${index}`"
                    v-model="ingredient.ingredientId"
                    required
                    @change="updateIngredientName(index)"
                  >
                    <option value="" disabled>Select ingredient</option>
                    <option v-for="ing in availableIngredients" :key="ing.id" :value="ing.id">
                      {{ ing.name }}
                    </option>
                  </select>
                </div>

                <div class="ingredient-quantity">
                  <label :for="`quantity-${index}`">Quantity *</label>
                  <input
                    type="number"
                    :id="`quantity-${index}`"
                    v-model="ingredient.quantity"
                    step="0.01"
                    min="0.01"
                    required
                  />
                </div>

                <div class="ingredient-unit">
                  <label :for="`unit-${index}`">Unit *</label>
                  <select :id="`unit-${index}`" v-model="ingredient.unit" required>
                    <option value="" disabled>Select unit</option>
                    <option value="g">Grams (g)</option>
                    <option value="kg">Kilograms (kg)</option>
                    <option value="ml">Milliliters (ml)</option>
                    <option value="l">Liters (l)</option>
                    <option value="tsp">Teaspoon (tsp)</option>
                    <option value="tbsp">Tablespoon (tbsp)</option>
                    <option value="cup">Cup</option>
                    <option value="piece">Piece</option>
                    <option value="slice">Slice</option>
                    <option value="pinch">Pinch</option>
                    <option value="to taste">To taste</option>
                  </select>
                </div>

                <div class="ingredient-notes">
                  <label :for="`notes-${index}`">Notes</label>
                  <input
                    type="text"
                    :id="`notes-${index}`"
                    v-model="ingredient.notes"
                    placeholder="Optional notes"
                  />
                </div>

                <button
                  type="button"
                  class="btn-icon delete"
                  @click="removeIngredient(index)"
                  :disabled="recipeForm.ingredients.length <= 1"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>

              <button type="button" class="btn-secondary btn-add" @click="addIngredient">
                <i class="fas fa-plus"></i> Add Another Ingredient
              </button>

              <span class="error-message" v-if="formErrors.ingredients">{{ formErrors.ingredients }}</span>
            </div>

            <!-- Instructions Section -->
            <div class="form-section">
              <h3>Instructions</h3>
              <p class="section-description">Provide step-by-step instructions for preparing your recipe</p>

              <div class="form-group">
                <label for="recipe-instructions">Cooking Instructions *</label>
                <textarea
                  id="recipe-instructions"
                  v-model="recipeForm.instructions"
                  rows="8"
                  required
                  :class="{ 'error': formErrors.instructions }"
                  placeholder="Enter detailed cooking instructions..."
                ></textarea>
                <span class="error-message" v-if="formErrors.instructions">{{ formErrors.instructions }}</span>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeRecipeFormModal">Cancel</button>
              <button type="submit" class="btn-primary">
                {{ isEditing ? 'Update Recipe' : 'Create Recipe' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  getUserRecipes,
  getRecipeById,
  createRecipe,
  updateRecipe,
  deleteRecipe as apiDeleteRecipe,
  getAllIngredients
} from '@/services/api';

const router = useRouter();
const authStore = useAuthStore();

// Check if user is authenticated
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Redirect if not authenticated
if (!isAuthenticated.value) {
  router.push('/login');
}

// Enum values
const cuisineTypes = ['ITALIAN', 'FRENCH', 'CHINESE', 'INDIAN', 'MEXICAN', 'JAPANESE', 'THAI', 'MEDITERRANEAN', 'AMERICAN', 'MIDDLE_EASTERN'];
const mealTypes = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'DESSERT', 'APPETIZER'];
const dietTypes = ['VEGETARIAN', 'VEGAN', 'GLUTEN_FREE', 'DAIRY_FREE', 'KETO', 'PALEO', 'LOW_CARB', 'LOW_FAT'];
const cookingMethods = ['BAKING', 'GRILLING', 'FRYING', 'BOILING', 'STEAMING', 'ROASTING', 'SLOW_COOKING', 'PRESSURE_COOKING'];
const difficultyLevels = ['EASY', 'MEDIUM', 'HARD'];

// Recipes state
const recipes = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = ref(12);
const totalPages = ref(0);
const totalElements = ref(0);
const searchQuery = ref('');
const filters = reactive({
  cuisine: '',
  mealType: ''
});
const sortOption = ref('newest');

// Modal states
const showDeleteModal = ref(false);
const showRecipeFormModal = ref(false);
const recipeToDelete = ref(null);
const isEditing = ref(false);
const editingRecipeId = ref(null);

// Form state
const recipeForm = reactive({
  name: '',
  description: '',
  cuisine: '',
  mealType: '',
  cookingTime: 30,
  dietType: '',
  cookingMethod: '',
  difficulty: 'MEDIUM',
  ingredients: [
    { ingredientId: '', ingredientName: '', quantity: 1, unit: '', notes: '' }
  ],
  instructions: '',
  imageUrl: ''
});
const formErrors = ref({});

// Available ingredients for selection
const availableIngredients = ref([]);

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

// Format enum values
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

// Fetch user recipes
const fetchUserRecipes = async () => {
  loading.value = true;

  try {
    // This would be a custom API endpoint to get the current user's recipes
    const response = await getUserRecipes(
      currentPage.value,
      pageSize.value,
      searchQuery.value,
      filters.cuisine,
      filters.mealType,
      sortOption.value
    );

    recipes.value = response.data.content;
    totalPages.value = response.data.totalPages;
    totalElements.value = response.data.totalElements;

  } catch (error) {
    console.error('Error fetching user recipes:', error);
  } finally {
    loading.value = false;
  }
};

// Fetch available ingredients
const fetchIngredients = async () => {
  try {
    const response = await getAllIngredients(0, 1000); // Get all ingredients
    availableIngredients.value = response.data.content;
  } catch (error) {
    console.error('Error fetching ingredients:', error);
  }
};

// Change page
const changePage = (page) => {
  currentPage.value = page;
  fetchUserRecipes();
};

// Debounce search
let searchTimeout;
const debounceSearch = () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    currentPage.value = 0;
    fetchUserRecipes();
  }, 300);
};

// Filter recipes
const filterRecipes = () => {
  currentPage.value = 0;
  fetchUserRecipes();
};

// Reset filters
const resetFilters = () => {
  searchQuery.value = '';
  filters.cuisine = '';
  filters.mealType = '';
  sortOption.value = 'newest';
  currentPage.value = 0;
  fetchUserRecipes();
};

// View recipe details
const viewRecipeDetails = (recipeId) => {
  router.push(`/recipe/${recipeId}`);
};

// Open create modal
const openCreateModal = () => {
  isEditing.value = false;
  editingRecipeId.value = null;

  // Reset form
  Object.assign(recipeForm, {
    name: '',
    description: '',
    cuisine: '',
    mealType: '',
    cookingTime: 30,
    dietType: '',
    cookingMethod: '',
    difficulty: 'MEDIUM',
    ingredients: [
      { ingredientId: '', ingredientName: '', quantity: 1, unit: '', notes: '' }
    ],
    instructions: '',
    imageUrl: ''
  });

  formErrors.value = {};
  showRecipeFormModal.value = true;
};

// Open edit modal
const openEditModal = async (recipe) => {
  isEditing.value = true;
  editingRecipeId.value = recipe.id;
  formErrors.value = {};

  try {
    // Fetch full recipe details
    const response = await getRecipeById(recipe.id);
    const recipeDetail = response.data;

    // Populate form with recipe data
    recipeForm.name = recipeDetail.name;
    recipeForm.description = recipeDetail.description || '';
    recipeForm.cuisine = recipeDetail.cuisine;
    recipeForm.mealType = recipeDetail.mealType;
    recipeForm.cookingTime = recipeDetail.cookingTime;
    recipeForm.dietType = recipeDetail.dietType || '';
    recipeForm.cookingMethod = recipeDetail.cookingMethod || '';
    recipeForm.difficulty = recipeDetail.difficulty || 'MEDIUM';
    recipeForm.instructions = recipeDetail.instructions;
    recipeForm.imageUrl = recipeDetail.imageUrl || '';

    // Handle ingredients
    if (recipeDetail.ingredients && recipeDetail.ingredients.length > 0) {
      recipeForm.ingredients = recipeDetail.ingredients.map(ing => ({
        ingredientId: ing.ingredientId,
        ingredientName: ing.ingredientName,
        quantity: ing.quantity,
        unit: ing.unit,
        notes: ing.notes || ''
      }));
    } else {
      recipeForm.ingredients = [
        { ingredientId: '', ingredientName: '', quantity: 1, unit: '', notes: '' }
      ];
    }

    showRecipeFormModal.value = true;

  } catch (error) {
    console.error('Error fetching recipe details for editing:', error);
  }
};

// Close recipe form modal
const closeRecipeFormModal = () => {
  showRecipeFormModal.value = false;
};

// Add ingredient row
const addIngredient = () => {
  recipeForm.ingredients.push({
    ingredientId: '',
    ingredientName: '',
    quantity: 1,
    unit: '',
    notes: ''
  });
};

// Remove ingredient row
const removeIngredient = (index) => {
  if (recipeForm.ingredients.length > 1) {
    recipeForm.ingredients.splice(index, 1);
  }
};

// Update ingredient name when ID changes
const updateIngredientName = (index) => {
  const selectedId = recipeForm.ingredients[index].ingredientId;
  if (selectedId) {
    const ingredient = availableIngredients.value.find(ing => ing.id === selectedId);
    if (ingredient) {
      recipeForm.ingredients[index].ingredientName = ingredient.name;
    }
  }
};

// Submit recipe form
const submitRecipeForm = async () => {
  formErrors.value = {};

  // Validate form
  let isValid = true;

  if (!recipeForm.name.trim()) {
    formErrors.value.name = 'Recipe name is required';
    isValid = false;
  }

  if (!recipeForm.cuisine) {
    formErrors.value.cuisine = 'Cuisine type is required';
    isValid = false;
  }

  if (!recipeForm.mealType) {
    formErrors.value.mealType = 'Meal type is required';
    isValid = false;
  }

  if (!recipeForm.cookingTime || recipeForm.cookingTime < 1) {
    formErrors.value.cookingTime = 'Cooking time must be at least 1 minute';
    isValid = false;
  }

  if (!recipeForm.instructions.trim()) {
    formErrors.value.instructions = 'Instructions are required';
    isValid = false;
  }

  // Validate ingredients
  const validIngredients = recipeForm.ingredients.filter(ing =>
    ing.ingredientId && ing.quantity && ing.unit
  );

  if (validIngredients.length === 0) {
    formErrors.value.ingredients = 'At least one complete ingredient is required';
    isValid = false;
  }

  if (!isValid) return;

  // Filter out incomplete ingredients
  const ingredients = recipeForm.ingredients.filter(ing =>
    ing.ingredientId && ing.quantity && ing.unit
  );

  try {
    if (isEditing.value) {
      await updateRecipe(editingRecipeId.value, {
        ...recipeForm,
        ingredients
      });
    } else {
      await createRecipe({
        ...recipeForm,
        ingredients
      });
    }

    // Refresh recipes
    fetchUserRecipes();
    closeRecipeFormModal();

  } catch (error) {
    console.error('Error saving recipe:', error);
    if (error.response && error.response.data) {
      // Handle validation errors from backend
      const backendErrors = error.response.data;
      Object.keys(backendErrors).forEach(key => {
        formErrors.value[key] = backendErrors[key];
      });
    }
  }
};

// Confirm delete
const confirmDelete = (recipe) => {
  recipeToDelete.value = recipe;
  showDeleteModal.value = true;
};

// Cancel delete
const cancelDelete = () => {
  showDeleteModal.value = false;
  recipeToDelete.value = null;
};

// Delete recipe
const deleteRecipe = async () => {
  if (!recipeToDelete.value) return;

  try {
    await apiDeleteRecipe(recipeToDelete.value.id);
    fetchUserRecipes();
    showDeleteModal.value = false;
    recipeToDelete.value = null;
  } catch (error) {
    console.error('Error deleting recipe:', error);
  }
};

// Initialize
onMounted(() => {
  fetchUserRecipes();
  fetchIngredients();
});

// Watch for changes in search query
watch([searchQuery], () => {
  currentPage.value = 0;
});
</script>

<style scoped>
.my-recipes-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  color: var(--text-color);
}

/* Filter and Sort Bar */
.filter-sort-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
}

.search-box {
  flex: 1;
  min-width: 250px;
  position: relative;
}

.search-box input {
  width: 100%;
  padding: 0.8rem 1rem 0.8rem 2.5rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.search-icon {
  position: absolute;
  left: 0.8rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-light);
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.filter-select {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-select label {
  color: var(--text-light);
  white-space: nowrap;
}

.filter-select select {
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* Recipes Grid */
.recipes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
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
  height: 200px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
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
  padding: 1.2rem;
}

.recipe-info h3 {
  margin: 0 0 0.8rem;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--text-color);
}

.recipe-info h3:hover {
  color: var(--primary-color);
}

.recipe-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
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

.recipe-description {
  margin: 0 0 1rem;
  font-size: 0.9rem;
  color: var(--text-light);
  line-height: 1.5;
  min-height: 2.7rem;
}

.recipe-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
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

.recipe-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
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
  font-size: 3rem;
  color: var(--text-light);
  margin-bottom: 1rem;
}

.empty-state p {
  margin-bottom: 1.5rem;
  color: var(--text-light);
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

/* Modal Styles */
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

.modal-content {
  background-color: var(--card-bg);
  border-radius: 16px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.4s ease-out;
  padding: 2rem;
}

.delete-modal {
  max-width: 400px;
  text-align: center;
}

.recipe-form-modal {
  max-width: 800px;
}

@keyframes slideUp {
  from { transform: translateY(50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.close-modal {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.1);
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s;
}

.close-modal:hover {
  background: rgba(0, 0, 0, 0.2);
  transform: scale(1.1);
}

/* Recipe Form */
.recipe-form h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.form-section {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid var(--border-color);
}

.form-section:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.form-section h3 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.section-description {
  color: var(--text-light);
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

.form-group input.error,
.form-group select.error,
.form-group textarea.error {
  border-color: #f44336;
}

.error-message {
  color: #f44336;
  font-size: 0.9rem;
  margin-top: 0.3rem;
  display: block;
}

.image-preview {
  margin-top: 1rem;
  border-radius: 8px;
  overflow: hidden;
  max-height: 200px;
}

.image-preview img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

/* Ingredient Rows */
.ingredient-row {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 1rem;
  align-items: flex-end;
}

.ingredient-select {
  flex: 2;
}

.ingredient-quantity {
  flex: 1;
}

.ingredient-unit {
  flex: 1;
}

.ingredient-notes {
  flex: 2;
}

.btn-add {
  margin-top: 1rem;
  width: 100%;
}

.form-actions, .modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

/* Button Styles */
.btn-primary, .btn-secondary, .btn-danger {
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

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.btn-secondary:hover {
  background-color: var(--border-color);
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-danger:hover {
  background-color: #d32f2f;
}

.btn-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon:hover {
  background-color: var(--border-color);
}

.btn-icon.delete {
  color: #f44336;
}

.btn-icon.delete:hover {
  background-color: rgba(244, 67, 54, 0.1);
}

.btn-icon.view {
  color: #2196F3;
}

.btn-icon.view:hover {
  background-color: rgba(33, 150, 243, 0.1);
}

.warning {
  color: #f44336;
  font-weight: 500;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .my-recipes-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .filter-sort-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-options {
    flex-direction: column;
  }

  .filter-select {
    width: 100%;
  }

  .form-row {
    flex-direction: column;
    gap: 1.5rem;
  }

  .ingredient-row {
    flex-wrap: wrap;
  }

  .ingredient-select {
    flex: 1 0 100%;
  }

  .ingredient-quantity, .ingredient-unit {
    flex: 1 0 45%;
  }

  .ingredient-notes {
    flex: 1 0 100%;
  }

  .modal-content {
    padding: 1.5rem;
    width: 95%;
  }

  .form-actions, .modal-actions {
    flex-direction: column-reverse;
  }

  .btn-primary, .btn-secondary, .btn-danger {
    width: 100%;
    justify-content: center;
  }
}
</style>
