<!-- src/views/IngredientsView.vue -->
<template>
  <div class="ingredients-page">
    <div class="page-header">
      <h1>Ingredients</h1>
      <button class="btn-primary" @click="openCreateModal">
        <i class="fas fa-plus"></i> Add New Ingredient
      </button>
    </div>

    <!-- Search and Filter Bar -->
    <div class="search-filter-bar">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search ingredients..."
          @input="debounceSearch"
        />
        <i class="fas fa-search search-icon"></i>
      </div>

      <div class="filter-box">
        <label for="category-filter">Filter by Category:</label>
        <select id="category-filter" v-model="selectedCategory" @change="filterByCategory">
          <option value="">All Categories</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ formatCategory(category) }}
          </option>
        </select>
      </div>
    </div>

    <!-- Ingredients Grid -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading ingredients...</p>
    </div>

    <div v-else-if="ingredients.length === 0" class="empty-state">
      <i class="fas fa-pepper-hot"></i>
      <p v-if="searchQuery || selectedCategory">No ingredients found matching your criteria</p>
      <p v-else>No ingredients available</p>
      <button @click="resetFilters" class="btn-secondary" v-if="searchQuery || selectedCategory">
        Clear Filters
      </button>
    </div>

    <div v-else class="ingredients-grid">
      <div
        v-for="ingredient in ingredients"
        :key="ingredient.id"
        class="ingredient-card"
        @click="viewIngredientDetails(ingredient)"
      >
        <div class="ingredient-category" :class="ingredient.category.toLowerCase()">
          {{ formatCategory(ingredient.category) }}
        </div>
        <h3>{{ ingredient.name }}</h3>
        <p class="nutritional-value" v-if="ingredient.nutritionalValue">
          {{ truncateText(ingredient.nutritionalValue, 100) }}
        </p>
        <p class="nutritional-value empty" v-else>
          No nutritional information available
        </p>
        <div class="substitutes" v-if="ingredient.substitutes && ingredient.substitutes.length > 0">
          <span class="substitutes-label">Substitutes:</span>
          <div class="substitute-tags">
            <span
              v-for="substitute in ingredient.substitutes.slice(0, 3)"
              :key="substitute.id"
              class="substitute-tag"
            >
              {{ substitute.name }}
            </span>
            <span class="more-tag" v-if="ingredient.substitutes.length > 3">
              +{{ ingredient.substitutes.length - 3 }} more
            </span>
          </div>
        </div>
        <div class="ingredient-actions"  @click.stop>
          <button class="btn-icon" @click.stop="openEditModal(ingredient)">
            <i class="fas fa-edit"></i>
          </button>
          <button class="btn-icon delete" @click.stop="confirmDelete(ingredient)">
            <i class="fas fa-trash-alt"></i>
          </button>
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

    <!-- Ingredient Detail Modal -->
    <div class="modal-overlay" v-if="showDetailModal" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeDetailModal">
          <i class="fas fa-times"></i>
        </button>

        <div v-if="selectedIngredient" class="ingredient-detail">
          <div class="ingredient-header">
            <h2>{{ selectedIngredient.name }}</h2>
            <span class="ingredient-category-badge" :class="selectedIngredient.category.toLowerCase()">
              {{ formatCategory(selectedIngredient.category) }}
            </span>
          </div>

          <div class="detail-section">
            <h3>Nutritional Value</h3>
            <p v-if="selectedIngredient.nutritionalValue">{{ selectedIngredient.nutritionalValue }}</p>
            <p class="empty-text" v-else>No nutritional information available</p>
          </div>

          <div class="detail-section">
            <h3>Substitutes</h3>
            <div v-if="selectedIngredient.substitutes && selectedIngredient.substitutes.length > 0">
              <div class="substitutes-list">
                <div
                  v-for="substitute in selectedIngredient.substitutes"
                  :key="substitute.id"
                  class="substitute-item"
                >
                  <span>{{ substitute.name }}</span>
                  <button
                    class="btn-icon delete"
                    @click="removeSubstitute(selectedIngredient.id, substitute.id)"
                  >
                    <i class="fas fa-times"></i>
                  </button>
                </div>
              </div>
            </div>
            <p class="empty-text" v-else>No substitutes available</p>

            <div class="add-substitute" >
              <h4>Add Substitute</h4>
              <div class="add-substitute-form">
                <select v-model="selectedSubstituteId">
                  <option value="" disabled>Select an ingredient</option>
                  <option
                    v-for="ingredient in availableSubstitutes"
                    :key="ingredient.id"
                    :value="ingredient.id"
                  >
                    {{ ingredient.name }}
                  </option>
                </select>
                <button
                  class="btn-secondary"
                  @click="addSubstitute"
                  :disabled="!selectedSubstituteId"
                >
                  Add
                </button>
              </div>
            </div>
          </div>

          <div class="modal-actions">
            <button class="btn-secondary" @click="openEditModal(selectedIngredient)">
              <i class="fas fa-edit"></i> Edit Ingredient
            </button>
            <button class="btn-danger" @click="confirmDelete(selectedIngredient)">
              <i class="fas fa-trash-alt"></i> Delete Ingredient
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Ingredient Modal -->
    <div class="modal-overlay" v-if="showFormModal" @click="closeFormModal">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeFormModal">
          <i class="fas fa-times"></i>
        </button>

        <div class="ingredient-form">
          <h2>{{ isEditing ? 'Edit Ingredient' : 'Add New Ingredient' }}</h2>

          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label for="ingredient-name">Name *</label>
              <input
                type="text"
                id="ingredient-name"
                v-model="form.name"
                required
                :class="{ 'error': errors.name }"
              />
              <span class="error-message" v-if="errors.name">{{ errors.name }}</span>
            </div>

            <div class="form-group">
              <label for="ingredient-category">Category *</label>
              <select
                id="ingredient-category"
                v-model="form.category"
                required
                :class="{ 'error': errors.category }"
              >
                <option value="" disabled>Select a category</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ formatCategory(category) }}
                </option>
              </select>
              <span class="error-message" v-if="errors.category">{{ errors.category }}</span>
            </div>

            <div class="form-group">
              <label for="ingredient-nutrition">Nutritional Value</label>
              <textarea
                id="ingredient-nutrition"
                v-model="form.nutritionalValue"
                rows="4"
                placeholder="Enter nutritional information..."
              ></textarea>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeFormModal">Cancel</button>
              <button type="submit" class="btn-primary">
                {{ isEditing ? 'Update' : 'Create' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" v-if="showDeleteModal" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <h2>Delete Ingredient</h2>
        <p>Are you sure you want to delete <strong>{{ ingredientToDelete?.name }}</strong>?</p>
        <p class="warning">This action cannot be undone.</p>

        <div class="modal-actions">
          <button class="btn-secondary" @click="cancelDelete">Cancel</button>
          <button class="btn-danger" @click="deleteIngredient">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';

import {
  getAllIngredients,
  getIngredientById,
  getAllCategories,
  createIngredient,
  updateIngredient,
  deleteIngredient as apiDeleteIngredient,
  addSubstitute as apiAddSubstitute,
  removeSubstitute as apiRemoveSubstitute
} from '@/services/api';

// Ingredients state
const ingredients = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = ref(20);
const totalPages = ref(0);
const totalElements = ref(0);
const searchQuery = ref('');
const selectedCategory = ref('');
const categories = ref([]);

// Modal states
const showDetailModal = ref(false);
const showFormModal = ref(false);
const showDeleteModal = ref(false);
const selectedIngredient = ref(null);
const ingredientToDelete = ref(null);
const editingIngredientId = ref(null);
const isEditing = ref(false);
const selectedSubstituteId = ref('');

// Form state
const form = ref({
  name: '',
  category: '',
  nutritionalValue: ''
});
const errors = ref({});

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

// Available substitutes (excluding current ingredient)
const availableSubstitutes = computed(() => {
  if (!selectedIngredient.value) return [];

  // Filter out the current ingredient and any existing substitutes
  return ingredients.value.filter(ingredient => {
    // Check if it's not the current ingredient
    if (ingredient.id === selectedIngredient.value.id) return false;

    // Check if it's not already a substitute
    const isAlreadySubstitute = selectedIngredient.value.substitutes.some(
      sub => sub.id === ingredient.id
    );

    return !isAlreadySubstitute;
  });
});

// Format category name
const formatCategory = (category) => {
  if (!category) return '';
  return category
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, char => char.toUpperCase());
};

// Truncate text
const truncateText = (text, maxLength) => {
  if (!text) return '';
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};

// Fetch ingredients
const fetchIngredients = async () => {
  loading.value = true;

  try {
    const response = await getAllIngredients(
      currentPage.value,
      pageSize.value,
      searchQuery.value,
      selectedCategory.value
    );

    ingredients.value = response.data.content;
    totalPages.value = response.data.totalPages;
    totalElements.value = response.data.totalElements;

  } catch (error) {
    console.error('Error fetching ingredients:', error);
  } finally {
    loading.value = false;
  }
};

// Fetch categories
const fetchCategories = async () => {
  try {
    const response = await getAllCategories();
    categories.value = response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
  }
};

// Change page
const changePage = (page) => {
  currentPage.value = page;
  fetchIngredients();
};

// Debounce search
let searchTimeout;
const debounceSearch = () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    currentPage.value = 0;
    fetchIngredients();
  }, 300);
};

// Filter by category
const filterByCategory = () => {
  currentPage.value = 0;
  fetchIngredients();
};

// Reset filters
const resetFilters = () => {
  searchQuery.value = '';
  selectedCategory.value = '';
  currentPage.value = 0;
  fetchIngredients();
};

// View ingredient details
const viewIngredientDetails = async (ingredient) => {
  try {
    // Fetch full ingredient details including substitutes
    const response = await getIngredientById(ingredient.id);
    selectedIngredient.value = response.data;
    showDetailModal.value = true;
  } catch (error) {
    console.error('Error fetching ingredient details:', error);
  }
};

// Close detail modal
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedIngredient.value = null;
  selectedSubstituteId.value = '';
};

// Open create modal
const openCreateModal = () => {
  isEditing.value = false;
  form.value = {
    name: '',
    category: '',
    nutritionalValue: ''
  };
  errors.value = {};
  showFormModal.value = true;
};

// Open edit modal
const openEditModal = (ingredient) => {
  isEditing.value = true;
  editingIngredientId.value = ingredient.id; // Store the ID separately
  form.value = {
    name: ingredient.name,
    category: ingredient.category,
    nutritionalValue: ingredient.nutritionalValue || ''
  };
  errors.value = {};
  showFormModal.value = true;

  // If we're in the detail modal, close it
  showDetailModal.value = false;
};

// Close form modal
const closeFormModal = () => {
  showFormModal.value = false;
  // Reset the editing ID when closing the modal
  if (isEditing.value) {
    editingIngredientId.value = null;
  }
};

// Submit form
const submitForm = async () => {
  errors.value = {};

  // Validate form
  if (!form.value.name.trim()) {
    errors.value.name = 'Name is required';
    return;
  }

  if (!form.value.category) {
    errors.value.category = 'Category is required';
    return;
  }

  try {
    if (isEditing.value) {
      // Use the stored ID for updating
      if (!editingIngredientId.value) {
        console.error('Missing ingredient ID for update');
        return;
      }
      await updateIngredient(editingIngredientId.value, form.value);
    } else {
      await createIngredient(form.value);
    }

    // Refresh ingredients
    fetchIngredients();
    closeFormModal();

    // If we were editing the currently selected ingredient, refresh its details
    if (selectedIngredient.value && selectedIngredient.value.id === editingIngredientId.value) {
      const response = await getIngredientById(editingIngredientId.value);
      selectedIngredient.value = response.data;
    }

  } catch (error) {
    console.error('Error saving ingredient:', error);
    if (error.response && error.response.data) {
      // Handle validation errors from backend
      const backendErrors = error.response.data;
      if (backendErrors.name) errors.value.name = backendErrors.name;
      if (backendErrors.category) errors.value.category = backendErrors.category;
    }
  }
};

// Confirm delete
const confirmDelete = (ingredient) => {
  ingredientToDelete.value = ingredient;
  showDeleteModal.value = true;

  // If we're in the detail modal, close it
  showDetailModal.value = false;
};

// Cancel delete
const cancelDelete = () => {
  showDeleteModal.value = false;
  ingredientToDelete.value = null;
};

// Delete ingredient
const deleteIngredient = async () => {
  if (!ingredientToDelete.value) return;

  try {
    await apiDeleteIngredient(ingredientToDelete.value.id);
    fetchIngredients();
    showDeleteModal.value = false;
    ingredientToDelete.value = null;
  } catch (error) {
    console.error('Error deleting ingredient:', error);
  }
};

// Add substitute
const addSubstitute = async () => {
  if (!selectedIngredient.value || !selectedSubstituteId.value) return;

  try {
    const response = await apiAddSubstitute(selectedIngredient.value.id, selectedSubstituteId.value);
    selectedIngredient.value = response.data;
    selectedSubstituteId.value = '';
  } catch (error) {
    console.error('Error adding substitute:', error);
  }
};

// Remove substitute
const removeSubstitute = async (ingredientId, substituteId) => {
  try {
    const response = await apiRemoveSubstitute(ingredientId, substituteId);
    selectedIngredient.value = response.data;
  } catch (error) {
    console.error('Error removing substitute:', error);
  }
};

// Initialize
onMounted(() => {
  fetchIngredients();
  fetchCategories();
});

// Watch for changes in search query
watch([searchQuery, selectedCategory], () => {
  currentPage.value = 0;
});
</script>

<style scoped>
.ingredients-page {
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

/* Search and Filter Bar */
.search-filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
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

.filter-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-box label {
  color: var(--text-light);
  white-space: nowrap;
}

.filter-box select {
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* Ingredients Grid */
.ingredients-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.ingredient-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: var(--card-bg);
  padding: 1.5rem;
  cursor: pointer;
}

.ingredient-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px var(--shadow-color);
}

.ingredient-category {
  position: absolute;
  top: 0;
  right: 0;
  padding: 0.3rem 0.8rem;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
  border-bottom-left-radius: 8px;
}

/* Category colors */
.vegetables {
  background-color: #4CAF50;
}

.fruits {
  background-color: #FF9800;
}

.grains {
  background-color: #FFC107;
}

.proteins {
  background-color: #F44336;
}

.dairy {
  background-color: #2196F3;
}

.herbs_and_spices {
  background-color: #9C27B0;
}

.oils_and_fats {
  background-color: #FF5722;
}

.sweeteners {
  background-color: #E91E63;
}

.nuts_and_seeds {
  background-color: #795548;
}

.condiments {
  background-color: #607D8B;
}

.ingredient-card h3 {
  margin: 0 0 0.8rem;
  font-size: 1.2rem;
  color: var(--text-color);
}

.nutritional-value {
  margin: 0 0 1rem;
  font-size: 0.9rem;
  color: var(--text-light);
  line-height: 1.5;
  min-height: 2.7rem;
}

.nutritional-value.empty {
  font-style: italic;
  opacity: 0.7;
}

.substitutes {
  margin-top: 1rem;
}

.substitutes-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-color);
  display: block;
  margin-bottom: 0.5rem;
}

.substitute-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.substitute-tag {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.more-tag {
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--text-light);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.ingredient-actions {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  display: flex;
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
  max-width: 600px;
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

/* Ingredient Detail */
.ingredient-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.ingredient-header h2 {
  margin: 0;
  color: var(--text-color);
}

.ingredient-category-badge {
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
}

.detail-section {
  margin-bottom: 2rem;
}

.detail-section h3 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 0.5rem;
}

.empty-text {
  color: var(--text-light);
  font-style: italic;
}

.substitutes-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.substitute-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem;
  background-color: rgba(76, 175, 80, 0.05);
  border-radius: 4px;
}

.add-substitute {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px dashed var(--border-color);
}

.add-substitute h4 {
  font-size: 1rem;
  margin-bottom: 0.8rem;
  color: var(--text-color);
}

.add-substitute-form {
  display: flex;
  gap: 0.5rem;
}

.add-substitute-form select {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* Form Styles */
.ingredient-form h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.form-group {
  margin-bottom: 1.5rem;
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
.form-group select.error {
  border-color: #f44336;
}

.error-message {
  color: #f44336;
  font-size: 0.9rem;
  margin-top: 0.3rem;
  display: block;
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

.warning {
  color: #f44336;
  font-weight: 500;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .ingredients-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .search-filter-bar {
    flex-direction: column;
  }

  .search-box, .filter-box {
    width: 100%;
  }

  .filter-box {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-box select {
    width: 100%;
  }

  .modal-content {
    padding: 1.5rem;
    width: 95%;
  }

  .ingredient-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .add-substitute-form {
    flex-direction: column;
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
