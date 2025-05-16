<!-- src/views/ShoppingListView.vue -->
<template>
  <div class="shopping-list-page">
    <div class="page-header">
      <h1>Shopping Lists</h1>
      <button class="btn-primary" @click="openCreateModal">
        <i class="fas fa-plus"></i> Create New Shopping List
      </button>
    </div>

    <!-- Shopping Lists Display -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading shopping lists...</p>
    </div>

    <div v-else-if="shoppingLists.length === 0" class="empty-state">
      <i class="fas fa-shopping-basket"></i>
      <p>You don't have any shopping lists yet</p>
      <button class="btn-primary" @click="openCreateModal">Create Your First List</button>
    </div>

    <div v-else class="shopping-lists">
      <div v-for="list in shoppingLists" :key="list.id" class="shopping-list-card" @click="viewListDetails(list)">
        <div class="list-status" :class="list.status.toLowerCase()">
          {{ formatStatus(list.status) }}
        </div>
        <div class="list-header">
          <h3>Shopping List</h3>
          <span class="list-date">{{ formatDate(list.createdAt) }}</span>
        </div>
        <div class="list-ingredients">
          <div v-if="list.ingredients.length === 0" class="empty-ingredients">
            No ingredients in this list
          </div>
          <div v-else class="ingredient-preview">
            <div v-for="(ingredient, index) in list.ingredients.slice(0, 3)" :key="ingredient.id" class="ingredient-item">
              {{ ingredient.name }}
            </div>
            <div v-if="list.ingredients.length > 3" class="more-ingredients">
              +{{ list.ingredients.length - 3 }} more
            </div>
          </div>
        </div>
        <div class="list-actions" @click.stop>
          <button class="btn-icon" @click.stop="updateListStatus(list)">
            <i class="fas" :class="list.status === 'ACTIVE' ? 'fa-check' : 'fa-redo'"></i>
          </button>
          <button class="btn-icon" @click.stop="openEditModal(list)">
            <i class="fas fa-edit"></i>
          </button>
          <button class="btn-icon delete" @click.stop="confirmDelete(list)">
            <i class="fas fa-trash-alt"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- Shopping List Detail Modal -->
    <div class="modal-overlay" v-if="showDetailModal" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeDetailModal">
          <i class="fas fa-times"></i>
        </button>

        <div v-if="selectedList" class="list-detail">
          <div class="list-detail-header">
            <h2>Shopping List</h2>
            <span class="list-status-badge" :class="selectedList.status.toLowerCase()">
              {{ formatStatus(selectedList.status) }}
            </span>
          </div>

          <div class="list-info">
            <p><strong>Created:</strong> {{ formatDate(selectedList.createdAt) }}</p>
            <p><strong>Last Updated:</strong> {{ formatDate(selectedList.updatedAt) }}</p>
          </div>

          <div class="detail-section">
            <h3>Ingredients</h3>
            <div v-if="selectedList.ingredients.length === 0" class="empty-text">
              No ingredients in this list
            </div>
            <div v-else class="ingredients-list">
              <div v-for="ingredient in selectedList.ingredients" :key="ingredient.id" class="ingredient-item-detail">
                <span>{{ ingredient.name }}</span>
                <button class="btn-icon delete" @click="removeIngredient(selectedList.id, ingredient.id)">
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </div>

            <div class="add-ingredient">
              <h4>Add Ingredients</h4>
              <div class="add-ingredient-form">
                <select v-model="selectedIngredientId">
                  <option value="" disabled>Select an ingredient</option>
                  <option v-for="ingredient in availableIngredients" :key="ingredient.id" :value="ingredient.id">
                    {{ ingredient.name }}
                  </option>
                </select>
                <button class="btn-secondary" @click="addIngredient" :disabled="!selectedIngredientId">
                  Add
                </button>
              </div>
            </div>
          </div>

          <div class="modal-actions">
            <button class="btn-secondary" @click="updateListStatus(selectedList)">
              <i class="fas" :class="selectedList.status === 'ACTIVE' ? 'fa-check' : 'fa-redo'"></i>
              {{ selectedList.status === 'ACTIVE' ? 'Mark as Completed' : 'Mark as Active' }}
            </button>
            <button class="btn-danger" @click="confirmDelete(selectedList)">
              <i class="fas fa-trash-alt"></i> Delete List
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Shopping List Modal -->
    <div class="modal-overlay" v-if="showCreateModal" @click="closeCreateModal">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeCreateModal">
          <i class="fas fa-times"></i>
        </button>

        <div class="shopping-list-form">
          <h2>Create New Shopping List</h2>

          <form @submit.prevent="createShoppingList">
            <div class="form-group">
              <label>Select Ingredients</label>
              <div v-if="allIngredients.length === 0" class="empty-text">
                No ingredients available. Please add ingredients first.
              </div>
              <div v-else class="ingredient-selection">
                <div v-for="ingredient in allIngredients" :key="ingredient.id" class="ingredient-checkbox">
                  <input
                    type="checkbox"
                    :id="`ingredient-${ingredient.id}`"
                    :value="ingredient.id"
                    v-model="selectedIngredients"
                  />
                  <label :for="`ingredient-${ingredient.id}`">{{ ingredient.name }}</label>
                </div>
              </div>
              <span class="error-message" v-if="errors.ingredients">{{ errors.ingredients }}</span>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeCreateModal">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="selectedIngredients.length === 0">
                Create List
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Edit Shopping List Modal -->
    <div class="modal-overlay" v-if="showEditModal" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeEditModal">
          <i class="fas fa-times"></i>
        </button>

        <div class="shopping-list-form">
          <h2>Edit Shopping List</h2>

          <form @submit.prevent="updateShoppingList">
            <div class="form-group">
              <label>Select Ingredients</label>
              <div v-if="allIngredients.length === 0" class="empty-text">
                No ingredients available.
              </div>
              <div v-else class="ingredient-selection">
                <div v-for="ingredient in allIngredients" :key="ingredient.id" class="ingredient-checkbox">
                  <input
                    type="checkbox"
                    :id="`edit-ingredient-${ingredient.id}`"
                    :value="ingredient.id"
                    v-model="editIngredients"
                  />
                  <label :for="`edit-ingredient-${ingredient.id}`">{{ ingredient.name }}</label>
                </div>
              </div>
              <span class="error-message" v-if="errors.ingredients">{{ errors.ingredients }}</span>
            </div>

            <div class="form-group">
              <label for="list-status">Status</label>
              <select id="list-status" v-model="editStatus">
                <option value="ACTIVE">Active</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-secondary" @click="closeEditModal">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="editIngredients.length === 0">
                Update List
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" v-if="showDeleteModal" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <h2>Delete Shopping List</h2>
        <p>Are you sure you want to delete this shopping list?</p>
        <p class="warning">This action cannot be undone.</p>

        <div class="modal-actions">
          <button class="btn-secondary" @click="cancelDelete">Cancel</button>
          <button class="btn-danger" @click="deleteShoppingList">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// Import API functions directly from your existing API file
import {
  getUserShoppingLists,
  getShoppingListById,
  createShoppingList as apiCreateShoppingList,
  updateShoppingList as apiUpdateShoppingList,
  updateShoppingListStatus as apiUpdateShoppingListStatus,
  deleteShoppingList as apiDeleteShoppingList,
  addIngredientsToShoppingList,
  removeIngredientFromShoppingList,
  getAllIngredients
} from '@/services/api';

// State
const shoppingLists = ref([]);
const allIngredients = ref([]);
const loading = ref(true);
const selectedList = ref(null);
const listToDelete = ref(null);

// Modal states
const showDetailModal = ref(false);
const showCreateModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);

// Form states
const selectedIngredients = ref([]);
const editIngredients = ref([]);
const editStatus = ref('ACTIVE');
const selectedIngredientId = ref('');
const errors = ref({});

// Computed properties
const availableIngredients = computed(() => {
  if (!selectedList.value) return [];

  // Filter out ingredients already in the list
  const existingIds = selectedList.value.ingredients.map(ing => ing.id);
  return allIngredients.value.filter(ing => !existingIds.includes(ing.id));
});

// Format date without using date-fns
const formatDate = (dateString) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  };

  return date.toLocaleDateString('en-US', options);
};

// Format status
const formatStatus = (status) => {
  if (!status) return '';
  return status.charAt(0) + status.slice(1).toLowerCase();
};

// Fetch shopping lists
const fetchShoppingLists = async () => {
  loading.value = true;
  try {
    const response = await getUserShoppingLists();
    shoppingLists.value = response.data;
  } catch (error) {
    console.error('Error fetching shopping lists:', error);
  } finally {
    loading.value = false;
  }
};

// Fetch all ingredients
const fetchAllIngredients = async () => {
  try {
    // Using the existing getAllIngredients function with a larger page size
    const response = await getAllIngredients(0, 100);
    allIngredients.value = response.data.content;
  } catch (error) {
    console.error('Error fetching ingredients:', error);
  }
};

// View list details
const viewListDetails = async (list) => {
  try {
    const response = await getShoppingListById(list.id);
    selectedList.value = response.data;
    showDetailModal.value = true;
  } catch (error) {
    console.error('Error fetching shopping list details:', error);
  }
};

// Close detail modal
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedList.value = null;
  selectedIngredientId.value = '';
};

// Open create modal
const openCreateModal = () => {
  selectedIngredients.value = [];
  errors.value = {};
  showCreateModal.value = true;
};

// Close create modal
const closeCreateModal = () => {
  showCreateModal.value = false;
  selectedIngredients.value = [];
  errors.value = {};
};

// Create shopping list
const createShoppingList = async () => {
  errors.value = {};

  if (selectedIngredients.value.length === 0) {
    errors.value.ingredients = 'Please select at least one ingredient';
    return;
  }

  try {
    await apiCreateShoppingList({ ingredientIds: selectedIngredients.value });
    fetchShoppingLists();
    closeCreateModal();
  } catch (error) {
    console.error('Error creating shopping list:', error);
    if (error.response && error.response.data) {
      errors.value = error.response.data;
    }
  }
};

// Open edit modal
const openEditModal = (list) => {
  // Close detail modal if open
  if (showDetailModal.value) {
    showDetailModal.value = false;
  }

  // Set edit form values
  editIngredients.value = list.ingredients.map(ing => ing.id);
  editStatus.value = list.status;
  listToDelete.value = list; // Store the list for editing
  errors.value = {};
  showEditModal.value = true;
};

// Close edit modal
const closeEditModal = () => {
  showEditModal.value = false;
  editIngredients.value = [];
  editStatus.value = 'ACTIVE';
  listToDelete.value = null;
  errors.value = {};
};

// Update shopping list
const updateShoppingList = async () => {
  errors.value = {};

  if (editIngredients.value.length === 0) {
    errors.value.ingredients = 'Please select at least one ingredient';
    return;
  }

  try {
    await apiUpdateShoppingList(listToDelete.value.id, {
      ingredientIds: editIngredients.value,
      status: editStatus.value
    });

    fetchShoppingLists();
    closeEditModal();
  } catch (error) {
    console.error('Error updating shopping list:', error);
    if (error.response && error.response.data) {
      errors.value = error.response.data;
    }
  }
};

// Update list status
const updateListStatus = async (list) => {
  const newStatus = list.status === 'ACTIVE' ? 'COMPLETED' : 'ACTIVE';

  try {
    await apiUpdateShoppingListStatus(list.id, { status: newStatus });

    // Update the list in our local state
    if (selectedList.value && selectedList.value.id === list.id) {
      selectedList.value.status = newStatus;
    }

    // Refresh the lists
    fetchShoppingLists();
  } catch (error) {
    console.error('Error updating shopping list status:', error);
  }
};

// Confirm delete
const confirmDelete = (list) => {
  listToDelete.value = list;
  showDeleteModal.value = true;

  // Close other modals if open
  if (showDetailModal.value) {
    showDetailModal.value = false;
  }
  if (showEditModal.value) {
    showEditModal.value = false;
  }
};

// Cancel delete
const cancelDelete = () => {
  showDeleteModal.value = false;
  listToDelete.value = null;
};

// Delete shopping list
const deleteShoppingList = async () => {
  if (!listToDelete.value) return;

  try {
    await apiDeleteShoppingList(listToDelete.value.id);
    fetchShoppingLists();
    showDeleteModal.value = false;
    listToDelete.value = null;
  } catch (error) {
    console.error('Error deleting shopping list:', error);
  }
};

// Add ingredient to shopping list
const addIngredient = async () => {
  if (!selectedList.value || !selectedIngredientId.value) return;

  try {
    const response = await addIngredientsToShoppingList(
      selectedList.value.id,
      { ingredientIds: [selectedIngredientId.value] }
    );

    selectedList.value = response.data;
    selectedIngredientId.value = '';
  } catch (error) {
    console.error('Error adding ingredient to shopping list:', error);
  }
};

// Remove ingredient from shopping list
const removeIngredient = async (listId, ingredientId) => {
  try {
    const response = await removeIngredientFromShoppingList(listId, ingredientId);
    selectedList.value = response.data;
  } catch (error) {
    console.error('Error removing ingredient from shopping list:', error);
  }
};

// Initialize
onMounted(() => {
  fetchShoppingLists();
  fetchAllIngredients();
});
</script>

<style scoped>
.shopping-list-page {
  max-width: 1200px;
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

/* Shopping Lists Grid */
.shopping-lists {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.shopping-list-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: var(--card-bg);
  padding: 1.5rem;
  cursor: pointer;
}

.shopping-list-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px var(--shadow-color);
}

.list-status {
  position: absolute;
  top: 0;
  right: 0;
  padding: 0.3rem 0.8rem;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
  border-bottom-left-radius: 8px;
}

/* Status colors */
.active {
  background-color: #4CAF50;
}

.completed {
  background-color: #9E9E9E;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.list-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: var(--text-color);
}

.list-date {
  font-size: 0.8rem;
  color: var(--text-light);
}

.list-ingredients {
  min-height: 80px;
}

.empty-ingredients {
  color: var(--text-light);
  font-style: italic;
  padding: 1rem 0;
}

.ingredient-preview {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.ingredient-item {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
  padding: 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.more-ingredients {
  text-align: center;
  color: var(--text-light);
  font-size: 0.8rem;
  margin-top: 0.5rem;
}

.list-actions {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  display: flex;
  gap: 0.5rem;
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

/* List Detail */
.list-detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.list-detail-header h2 {
  margin: 0;
  color: var(--text-color);
}

.list-status-badge {
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
}

.list-info {
  margin-bottom: 1.5rem;
  color: var(--text-light);
  font-size: 0.9rem;
}

.list-info p {
  margin: 0.3rem 0;
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
  padding: 1rem 0;
}

.ingredients-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.ingredient-item-detail {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 1rem;
  background-color: rgba(76, 175, 80, 0.05);
  border-radius: 4px;
}

.add-ingredient {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px dashed var(--border-color);
}

.add-ingredient h4 {
  font-size: 1rem;
  margin-bottom: 0.8rem;
  color: var(--text-color);
}

.add-ingredient-form {
  display: flex;
  gap: 0.5rem;
}

.add-ingredient-form select {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* Form Styles */
.shopping-list-form h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.8rem;
  font-weight: 500;
  color: var(--text-color);
}

.ingredient-selection {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
}

.ingredient-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.ingredient-checkbox input[type="checkbox"] {
  margin-right: 0.5rem;
}

.form-group select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.error-message {
  color: #f44336;
  font-size: 0.9rem;
  margin-top: 0.5rem;
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

.btn-primary:hover:not(:disabled) {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.btn-secondary:hover:not(:disabled) {
  background-color: var(--border-color);
}

.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  .shopping-list-page {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .add-ingredient-form {
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
