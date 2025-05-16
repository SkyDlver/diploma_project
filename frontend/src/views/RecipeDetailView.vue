<!-- src/views/RecipeDetailView.vue -->
<template>
  <div class="recipe-detail-page">
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading recipe...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <h2>Oops! Something went wrong</h2>
      <p>{{ error }}</p>
      <button class="btn-primary" @click="fetchRecipe">Try Again</button>
    </div>

    <!-- Recipe Content -->
    <div v-else-if="recipe" class="recipe-content">
      <!-- Recipe Header -->
      <div class="recipe-header" :style="{ backgroundImage: `url(${recipe.imageUrl})` }">
        <div class="recipe-header-overlay">
          <div class="recipe-header-content">
            <div class="recipe-badges">
              <span class="badge cuisine">{{ formatEnum(recipe.cuisine) }}</span>
              <span class="badge meal-type">{{ formatEnum(recipe.mealType) }}</span>
              <span class="badge diet-type" v-if="recipe.dietType">{{ formatEnum(recipe.dietType) }}</span>
            </div>
            <h1>{{ recipe.name }}</h1>
            <div class="recipe-meta">
              <div class="recipe-rating">
                <div class="stars">
                  <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'star-'+i"></i>
                  <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                  <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'empty-'+i"></i>
                </div>
                <span>{{ recipe.rating.toFixed(1) }} ({{ reviewsCount }} reviews)</span>
              </div>
              <div class="recipe-time">
                <i class="far fa-clock"></i> {{ recipe.cookingTime }} minutes
              </div>
              <div class="recipe-difficulty">
                <i class="fas fa-signal"></i> {{ formatEnum(recipe.difficulty) }}
              </div>
              <div class="recipe-method">
                <i class="fas fa-utensils"></i> {{ formatEnum(recipe.cookingMethod) }}
              </div>
            </div>
            <div class="recipe-actions">
              <button
                class="btn-favorite"
                @click="toggleFavorite"
                :class="{ 'is-favorite': recipe.isFavorite }"
              >
                <i class="fas" :class="recipe.isFavorite ? 'fa-heart' : 'fa-heart'"></i>
                {{ recipe.isFavorite ? 'Saved to Favorites' : 'Add to Favorites' }}
              </button>
              <button class="btn-share" @click="shareRecipe">
                <i class="fas fa-share-alt"></i> Share
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Recipe Details -->
      <div class="recipe-details">
        <div class="recipe-main">
          <!-- Description -->
          <section class="recipe-section">
            <h2>Description</h2>
            <p>{{ recipe.description }}</p>
            <div class="recipe-author" v-if="recipe.author">
              <p>By: {{ recipe.author.firstName }} {{ recipe.author.lastName }}</p>
            </div>
          </section>

          <!-- Ingredients -->
          <section class="recipe-section">
            <h2>Ingredients</h2>
            <ul class="ingredients-list">
              <li v-for="ingredient in recipe.ingredients" :key="ingredient.id">
                <span class="ingredient-quantity">{{ ingredient.quantity }} {{ ingredient.unit }}</span>
                <span class="ingredient-name">{{ ingredient.name }}</span>
              </li>
            </ul>
          </section>

          <!-- Instructions -->
          <section class="recipe-section">
            <h2>Instructions</h2>
            <div class="instructions-content">
              <div v-if="formattedInstructions.length > 0">
                <div v-for="(step, index) in formattedInstructions" :key="index" class="instruction-step">
                  <div class="step-number">{{ index + 1 }}</div>
                  <div class="step-content">{{ step }}</div>
                </div>
              </div>
              <div v-else class="instruction-text">
                {{ recipe.instructions }}
              </div>
            </div>
          </section>

          <!-- Reviews Section -->
          <section class="recipe-section reviews-section">
            <div class="section-header">
              <h2>Reviews</h2>
              <div class="reviews-summary">
                <div class="stars">
                  <i class="fas fa-star" v-for="i in Math.floor(recipe.rating)" :key="'sum-star-'+i"></i>
                  <i class="fas fa-star-half-alt" v-if="recipe.rating % 1 >= 0.5"></i>
                  <i class="far fa-star" v-for="i in (5 - Math.ceil(recipe.rating))" :key="'sum-empty-'+i"></i>
                </div>
                <span>{{ recipe.rating.toFixed(1) }} out of 5</span>
              </div>
            </div>

            <!-- Add Review Form -->
            <div v-if="isAuthenticated" class="add-review-container">
              <h3>Write a Review</h3>
              <form @submit.prevent="submitReview" class="review-form">
                <div class="rating-input">
                  <label>Your Rating:</label>
                  <div class="star-rating">
                    <i
                      v-for="star in 5"
                      :key="star"
                      :class="[
                      newReview.rating >= star ? 'fas fa-star' : 'far fa-star',
                      { 'active': newReview.rating >= star, 'hovered': hoverRating >= star }
                    ]"
                      @click="setRating(star)"
                      @mouseover="hoverRating = star"
                      @mouseleave="hoverRating = 0"
                    ></i>
                  </div>
                </div>
                <div class="form-group">
                  <label for="review-comment">Your Review (optional):</label>
                  <textarea
                    id="review-comment"
                    v-model="newReview.comment"
                    placeholder="Share your thoughts about this recipe..."
                    rows="4"
                  ></textarea>
                </div>
                <div class="form-actions">
                  <button type="submit" class="btn-primary" >
                    {{ editingReviewId ? 'Update Review' : 'Submit Review' }}
                  </button>
                  <button
                    v-if="editingReviewId"
                    type="button"
                    class="btn-secondary"
                    @click="cancelEdit"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
            <div v-else class="login-to-review">
              <p>Please <router-link to="/login">login</router-link> to leave a review.</p>
            </div>

            <!-- Reviews List -->
            <div class="reviews-list">
              <div v-if="loadingReviews" class="loading-reviews">
                <div class="loading-spinner"></div>
                <p>Loading reviews...</p>
              </div>

              <div v-else-if="reviews.length === 0" class="no-reviews">
                <i class="fas fa-comment-slash"></i>
                <p>No reviews yet. Be the first to review this recipe!</p>
              </div>

              <div v-else>
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <div class="review-user">
                      <div class="user-avatar">
                        {{ getUserInitials(review.user) }}
                      </div>
                      <div class="user-info">
                        <div class="user-name">
                          {{ review.user.firstName }} {{ review.user.lastName }}
                        </div>
                        <div class="review-date">
                          {{ formatDate(review.createdAt) }}
                        </div>
                      </div>
                    </div>
                    <div class="review-rating">
                      <div class="stars">
                        <i class="fas fa-star" v-for="i in review.rating" :key="'rev-star-'+i"></i>
                        <i class="far fa-star" v-for="i in (5 - review.rating)" :key="'rev-empty-'+i"></i>
                      </div>
                    </div>
                  </div>
                  <div class="review-content">
                    {{ review.comment || "No comment provided." }}
                  </div>
                  <div v-if="isCurrentUserReview(review)" class="review-actions">
                    <button class="btn-edit" @click="editReview(review)">
                      <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="btn-delete" @click="confirmDeleteReview(review)">
                      <i class="fas fa-trash-alt"></i> Delete
                    </button>
                  </div>
                </div>

                <!-- Pagination -->
                <div v-if="totalPages > 1" class="pagination">
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
            </div>
          </section>
        </div>

        <div class="recipe-sidebar">
          <!-- Recipe Info Card -->
          <div class="recipe-info-card">
            <h3>Recipe Info</h3>
            <div class="info-item">
              <span class="info-label">Prep Time:</span>
              <span class="info-value">{{ Math.floor(recipe.cookingTime * 0.3) }} minutes</span>
            </div>
            <div class="info-item">
              <span class="info-label">Cook Time:</span>
              <span class="info-value">{{ Math.floor(recipe.cookingTime * 0.7) }} minutes</span>
            </div>
            <div class="info-item">
              <span class="info-label">Total Time:</span>
              <span class="info-value">{{ recipe.cookingTime }} minutes</span>
            </div>
<!--            <div class="info-item">-->
<!--              <span class="info-label">Servings:</span>-->
<!--              <span class="info-value">4</span>-->
<!--            </div>-->
            <div class="info-item">
              <span class="info-label">Difficulty:</span>
              <span class="info-value">{{ formatEnum(recipe.difficulty) }}</span>
            </div>
          </div>

          <!-- Shopping List Action -->
          <div class="sidebar-action-card">
            <h3>Need Ingredients?</h3>
            <p>Add all ingredients to your shopping list with one click.</p>
            <button class="btn-primary full-width" @click="addToShoppingList">
              <i class="fas fa-shopping-basket"></i> Add to Shopping List
            </button>
          </div>

          <!-- Similar Recipes -->
          <!--          <div class="similar-recipes-card">-->
          <!--            <h3>You Might Also Like</h3>-->
          <!--            <div v-if="similarRecipes.length > 0" class="similar-recipes-list">-->
          <!--              <div v-for="similarRecipe in similarRecipes" :key="similarRecipe.id" class="similar-recipe-item">-->
          <!--                <router-link :to="`/recipe/${similarRecipe.id}`" class="similar-recipe-link">-->
          <!--                  <div class="similar-recipe-image">-->
          <!--                    <img :src="similarRecipe.imageUrl" :alt="similarRecipe.name">-->
          <!--                  </div>-->
          <!--                  <div class="similar-recipe-info">-->
          <!--                    <h4>{{ similarRecipe.name }}</h4>-->
          <!--                    <div class="similar-recipe-meta">-->
          <!--                      <span class="similar-recipe-time">-->
          <!--                        <i class="far fa-clock"></i> {{ similarRecipe.cookingTime }} min-->
          <!--                      </span>-->
          <!--                      <div class="similar-recipe-rating">-->
          <!--                        <i class="fas fa-star"></i> {{ similarRecipe.rating.toFixed(1) }}-->
          <!--                      </div>-->
          <!--                    </div>-->
          <!--                  </div>-->
          <!--                </router-link>-->
          <!--              </div>-->
          <!--            </div>-->
          <!--            <div v-else class="no-similar-recipes">-->
          <!--              No similar recipes found.-->
          <!--            </div>-->
          <!--          </div>-->
        </div>
      </div>
    </div>

    <!-- Delete Review Confirmation Modal -->
    <div class="modal-overlay" v-if="showDeleteModal" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <h2>Delete Review</h2>
        <p>Are you sure you want to delete your review?</p>
        <p class="warning">This action cannot be undone.</p>

        <div class="modal-actions">
          <button class="btn-secondary" @click="cancelDelete">Cancel</button>
          <button class="btn-danger" @click="deleteReviewConfirmed">Delete</button>
        </div>
      </div>
    </div>

    <!-- Share Modal -->
    <div class="modal-overlay" v-if="showShareModal" @click="showShareModal = false">
      <div class="modal-content share-modal" @click.stop>
        <button class="close-modal" @click="showShareModal = false">
          <i class="fas fa-times"></i>
        </button>
        <h2>Share this Recipe</h2>
        <div class="share-options">
          <button class="share-btn facebook">
            <i class="fab fa-facebook-f"></i> Facebook
          </button>
          <button class="share-btn twitter">
            <i class="fab fa-twitter"></i> Twitter
          </button>
          <button class="share-btn pinterest">
            <i class="fab fa-pinterest-p"></i> Pinterest
          </button>
          <button class="share-btn email">
            <i class="fas fa-envelope"></i> Email
          </button>
        </div>
        <div class="share-link">
          <input type="text" :value="currentUrl" readonly @click="selectText" ref="shareLink">
          <button class="btn-copy" @click="copyLink">
            <i class="fas fa-copy"></i> Copy
          </button>
        </div>
      </div>
    </div>

    <!-- Shopping List Modal -->
    <div class="modal-overlay" v-if="showShoppingListModal" @click="showShoppingListModal = false">
      <div class="modal-content shopping-list-modal" @click.stop>
        <button class="close-modal" @click="showShoppingListModal = false">
          <i class="fas fa-times"></i>
        </button>
        <h2>Add to Shopping List</h2>

        <div v-if="!isAuthenticated" class="login-required">
          <p>Please <router-link to="/login">login</router-link> to use the shopping list feature.</p>
        </div>

        <div v-else>
          <div v-if="loadingShoppingLists" class="loading-container">
            <div class="loading-spinner"></div>
            <p>Loading your shopping lists...</p>
          </div>

          <div v-else>
            <div class="shopping-list-options">
              <div class="option">
                <input
                  type="radio"
                  id="new-list"
                  value="new"
                  v-model="shoppingListOption"
                  name="shopping-list-option"
                >
                <label for="new-list">Create new shopping list</label>
              </div>

              <div class="option" v-if="userShoppingLists.length > 0">
                <input
                  type="radio"
                  id="existing-list"
                  value="existing"
                  v-model="shoppingListOption"
                  name="shopping-list-option"
                >
                <label for="existing-list">Add to existing list</label>

                <div v-if="shoppingListOption === 'existing'" class="existing-lists">
                  <select v-model="selectedShoppingListId">
                    <option value="" disabled>Select a shopping list</option>
                    <option
                      v-for="list in userShoppingLists"
                      :key="list.id"
                      :value="list.id"
                    >
                      Shopping List ({{ formatDate(list.createdAt) }})
                    </option>
                  </select>
                </div>
              </div>
            </div>

            <div class="ingredients-selection">
              <h3>Ingredients to add:</h3>
              <div class="ingredients-list">
                <div
                  v-for="ingredient in recipe.ingredients"
                  :key="ingredient.id"
                  class="ingredient-checkbox"
                >
                  <input
                    type="checkbox"
                    :id="`ingredient-${ingredient.id}`"
                    :value="ingredient.id"
                    v-model="selectedIngredients"
                  >
                  <label :for="`ingredient-${ingredient.id}`">
                    {{ ingredient.quantity }} {{ ingredient.unit }} {{ ingredient.name }}
                  </label>
                </div>
              </div>

              <div class="form-actions">
                <button
                  class="btn-secondary"
                  @click="showShoppingListModal = false"
                >
                  Cancel
                </button>
                <button
                  class="btn-primary"
                  @click="confirmAddToShoppingList"
                  :disabled="selectedIngredients.length === 0 ||
                            (shoppingListOption === 'existing' && !selectedShoppingListId)"
                >
                  Add to Shopping List
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  getRecipeById,
  favoriteRecipe,
  unfavoriteRecipe,
  getRecipeReviews,
  createReview,
  updateReview,
  deleteReview,
  getUserShoppingLists,
  createShoppingList,
  addIngredientsToShoppingList
} from '@/services/api';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// State
const recipe = ref(null);
const loading = ref(true);
const error = ref(null);
const reviews = ref([]);
const loadingReviews = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const reviewsCount = ref(0);
const similarRecipes = ref([]);

// Review form state
const newReview = ref({
  rating: 1,
  comment: ''
});
const hoverRating = ref(0);
const editingReviewId = ref(null);
const showDeleteModal = ref(false);
const reviewToDelete = ref(null);

// Share modal state
const showShareModal = ref(false);
const currentUrl = ref('');
const shareLink = ref(null);

// Shopping list modal state
const showShoppingListModal = ref(false);
const shoppingListOption = ref('new');
const selectedShoppingListId = ref('');
const selectedIngredients = ref([]);
const userShoppingLists = ref([]);
const loadingShoppingLists = ref(false);

// Check if user is authenticated
const isAuthenticated = computed(() => authStore.isAuthenticated);
const currentUser = computed(() => authStore.user);

// Format instructions into steps
const formattedInstructions = computed(() => {
  if (!recipe.value || !recipe.value.instructions) return [];

  // Try to split by numbered steps or line breaks
  const text = recipe.value.instructions;

  // Check if the text contains numbered steps (e.g., "1. Step one")
  if (/\d+\.\s/.test(text)) {
    return text.split(/\d+\.\s/).filter(step => step.trim().length > 0);
  }

  // Otherwise split by line breaks
  return text.split(/\r?\n/).filter(step => step.trim().length > 0);
});

// Fetch recipe details
const fetchRecipe = async () => {
  const recipeId = route.params.id;
  if (!recipeId) {
    error.value = 'Recipe ID is missing';
    loading.value = false;
    return;
  }


  loading.value = true;
  error.value = null;

  try {
    const response = await getRecipeById(recipeId);
    recipe.value = response.data;

    // Fetch similar recipes based on cuisine or meal type
    // This would be a separate API call in a real implementation
    fetchSimilarRecipes();

    // Fetch reviews
    fetchReviews();
  } catch (err) {
    console.error('Error fetching recipe:', err);
    error.value = 'Failed to load recipe. Please try again later.';
  } finally {
    loading.value = false;
  }
};
// Improve the toggleFavorite function
const toggleFavorite = async () => {
  if (!isAuthenticated.value || !recipe.value) {
    // Redirect to login if not authenticated
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }

  try {
    if (recipe.value.isFavorite) {
      await unfavoriteRecipe(recipe.value.id);
      recipe.value.isFavorite = false;
    } else {
      await favoriteRecipe(recipe.value.id);
      recipe.value.isFavorite = true;
    }
    console.log('Favorite status updated:', recipe.value.isFavorite);
  } catch (error) {
    console.error('Error toggling favorite:', error);
    alert('Failed to update favorite status. Please try again.');
  }
};
// Fetch reviews for the recipe
const fetchReviews = async (page = 0) => {
  if (!recipe.value) return;

  loadingReviews.value = true;
  currentPage.value = page;

  try {
    const response = await getRecipeReviews(recipe.value.id, page);
    reviews.value = response.data.content;
    totalPages.value = response.data.totalPages;
    reviewsCount.value = response.data.totalElements;
  } catch (err) {
    console.error('Error fetching reviews:', err);
  } finally {
    loadingReviews.value = false;
  }
};

// Change reviews page
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    fetchReviews(page);
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

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  };

  return date.toLocaleDateString('en-US', options);
};

// Get user initials for avatar
const getUserInitials = (user) => {
  if (!user) return '';
  return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
};

// Check if review is by current user
const isCurrentUserReview = (review) => {
  return currentUser.value && review.user && review.user.id === currentUser.value.id;
};

// Set rating in review form
const setRating = (rating) => {
  newReview.value.rating = rating;
};

// Submit review
const submitReview = async () => {
  if (!isAuthenticated.value || !recipe.value) return;

  try {
    if (editingReviewId.value) {
      // Update existing review
      await updateReview(editingReviewId.value, {
        rating: newReview.value.rating,
        comment: newReview.value.comment
      });
    } else {
      // Create new review
      await createReview(recipe.value.id, {
        rating: newReview.value.rating,
        comment: newReview.value.comment
      });
    }

    // Reset form
    newReview.value = { rating: 0, comment: '' };
    editingReviewId.value = null;

    // Refresh reviews
    fetchReviews();

    // Refresh recipe to update average rating
    fetchRecipe();
  } catch (err) {
    console.error('Error submitting review:', err);
    alert('Failed to submit review. Please try again.');
  }
};

// Edit review
const editReview = (review) => {
  editingReviewId.value = review.id;
  newReview.value = {
    rating: review.rating,
    comment: review.comment || ''
  };

  // Scroll to review form
  document.querySelector('.add-review-container').scrollIntoView({ behavior: 'smooth' });
};

// Cancel edit
const cancelEdit = () => {
  editingReviewId.value = null;
  newReview.value = { rating: 0, comment: '' };
};

// Confirm delete review
const confirmDeleteReview = (review) => {
  reviewToDelete.value = review;
  showDeleteModal.value = true;
};

// Cancel delete
const cancelDelete = () => {
  showDeleteModal.value = false;
  reviewToDelete.value = null;
};

// Delete review confirmed
const deleteReviewConfirmed = async () => {
  if (!reviewToDelete.value) return;

  try {
    await deleteReview(reviewToDelete.value.id);

    // Refresh reviews
    fetchReviews();

    // Refresh recipe to update average rating
    fetchRecipe();

    // Close modal
    showDeleteModal.value = false;
    reviewToDelete.value = null;
  } catch (err) {
    console.error('Error deleting review:', err);
    alert('Failed to delete review. Please try again.');
  }
};

// Share recipe
const shareRecipe = () => {
  currentUrl.value = window.location.href;
  showShareModal.value = true;
};

// Copy link to clipboard
const copyLink = () => {
  if (shareLink.value) {
    shareLink.value.select();
    document.execCommand('copy');
    alert('Link copied to clipboard!');
  }
};

// Select text in input
const selectText = (event) => {
  event.target.select();
};

// Add to shopping list
const addToShoppingList = async () => {
  if (!isAuthenticated.value) {
    // Redirect to login if not authenticated
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }

  // Reset state
  shoppingListOption.value = 'new';
  selectedShoppingListId.value = '';
  selectedIngredients.value = recipe.value.ingredients.map(ing => ing.id);

  // Fetch user's shopping lists
  loadingShoppingLists.value = true;
  try {
    const response = await getUserShoppingLists();
    userShoppingLists.value = response.data;

    // If user has shopping lists, default to adding to existing list
    if (userShoppingLists.value.length > 0) {
      shoppingListOption.value = 'existing';
      selectedShoppingListId.value = userShoppingLists.value[0].id;
    }
  } catch (err) {
    console.error('Error fetching shopping lists:', err);
  } finally {
    loadingShoppingLists.value = false;
  }

  showShoppingListModal.value = true;
};

// Confirm add to shopping list
const confirmAddToShoppingList = async () => {
  if (selectedIngredients.value.length === 0) return;

  try {
    if (shoppingListOption.value === 'new') {
      // Create new shopping list
      await createShoppingList({ ingredientIds: selectedIngredients.value });
    } else {
      // Add to existing list
      await addIngredientsToShoppingList(
        selectedShoppingListId.value,
        { ingredientIds: selectedIngredients.value }
      );
    }

    // Close modal
    showShoppingListModal.value = false;

    // Show success message
    alert('Ingredients added to shopping list successfully!');
  } catch (err) {
    console.error('Error adding to shopping list:', err);
    alert('Failed to add ingredients to shopping list. Please try again.');
  }
};

// Fetch similar recipes
const fetchSimilarRecipes = async () => {
  if (!recipe.value) return;

  // This would be a real API call in a production app
  // For now, we'll just simulate it with a timeout
  setTimeout(() => {
    // Generate some fake similar recipes based on the current recipe
    similarRecipes.value = [
      {
        id: '123e4567-e89b-12d3-a456-426614174001',
        name: 'Similar ' + recipe.value.name,
        imageUrl: recipe.value.imageUrl,
        cookingTime: recipe.value.cookingTime - 5,
        rating: Math.min(5, recipe.value.rating + 0.2)
      },
      {
        id: '123e4567-e89b-12d3-a456-426614174002',
        name: 'Another ' + recipe.value.name + ' Variation',
        imageUrl: recipe.value.imageUrl,
        cookingTime: recipe.value.cookingTime + 10,
        rating: Math.max(1, recipe.value.rating - 0.3)
      },
      {
        id: '123e4567-e89b-12d3-a456-426614174003',
        name: 'Easy ' + recipe.value.name,
        imageUrl: recipe.value.imageUrl,
        cookingTime: Math.floor(recipe.value.cookingTime * 0.7),
        rating: Math.min(5, recipe.value.rating + 0.1)
      }
    ];
  }, 1000);
};

// Watch for route changes to reload recipe
watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      fetchRecipe();
    }
  }
);

// Initialize
onMounted(() => {
  fetchRecipe();
});
</script>

<style scoped>
.recipe-detail-page {
  min-height: 100vh;
  background-color: var(--bg-color);
}

/* Loading and Error States */
.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
  text-align: center;
  min-height: 60vh;
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

.error-container i {
  font-size: 3rem;
  color: #f44336;
  margin-bottom: 1rem;
}

.error-container h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.error-container p {
  margin-bottom: 2rem;
  color: var(--text-light);
}

/* Recipe Header */
.recipe-header {
  height: 500px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.recipe-header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.8));
  display: flex;
  align-items: flex-end;
}

.recipe-header-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  color: white;
}

.recipe-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.badge {
  padding: 0.3rem 0.8rem;
  border-radius: 50px;
  font-size: 0.9rem;
  font-weight: 500;
}

.cuisine {
  background-color: rgba(76, 175, 80, 0.8);
}

.meal-type {
  background-color: rgba(33, 150, 243, 0.8);
}

.diet-type {
  background-color: rgba(156, 39, 176, 0.8);
}

.recipe-header-content h1 {
  font-size: 2.5rem;
  margin: 0 0 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.recipe-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
}

.recipe-rating, .recipe-time, .recipe-difficulty, .recipe-method {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  color: #FFD700;
}

.recipe-actions {
  display: flex;
  gap: 1rem;
}

.btn-favorite, .btn-share {
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

.btn-favorite {
  background-color: white;
  color: var(--primary-color);
}

.btn-favorite:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.btn-favorite.is-favorite {
  background-color: var(--primary-color);
  color: white;
}

.btn-share {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.btn-share:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

/* Recipe Details */
.recipe-details {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

.recipe-section {
  margin-bottom: 2.5rem;
}

.recipe-section h2 {
  font-size: 1.8rem;
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 0.5rem;
  display: inline-block;
}

.recipe-section p {
  line-height: 1.6;
  color: var(--text-color);
}

.recipe-author {
  margin-top: 1rem;
  font-style: italic;
  color: var(--text-light);
}

/* Ingredients */
.ingredients-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.ingredients-list li {
  padding: 0.8rem 0;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
}

.ingredient-quantity {
  font-weight: 600;
  margin-right: 0.5rem;
  min-width: 80px;
}

/* Instructions */
.instructions-content {
  counter-reset: step-counter;
}

.instruction-step {
  display: flex;
  margin-bottom: 1.5rem;
}

.step-number {
  width: 36px;
  height: 36px;
  background-color: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 1rem;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
  line-height: 1.6;
  padding-top: 0.5rem;
}

.instruction-text {
  line-height: 1.8;
  white-space: pre-line;
}

/* Reviews Section */
.reviews-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.reviews-summary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.add-review-container {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px var(--shadow-color);
}

.add-review-container h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.2rem;
  color: var(--text-color);
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}
.stars i.fas.fa-star {
  color: #FFD700;
}

.stars i.far.fa-star {
  color: #ccc;
}

.stars i.fas.fa-star-half-alt {
  color: #FFD700;
}

/* For the rating input */
.star-rating i.fas.fa-star {
  color: #FFD700;
}

.star-rating i.far.fa-star {
  color: #ccc;
}

.star-rating i.active {
  color: #FFD700;
}

.star-rating i.hovered {
  color: #FFD700;
  transform: scale(1.2);
}

.star-rating {
  display: flex;
  gap: 0.3rem;
}
.star-rating i {
  cursor: pointer;
  font-size: 1.5rem;
  color: #ccc;
  transition: all 0.2s;
}

.star-rating i.fas.fa-star {
  color: #FFD700;
}
.star-rating i.fas.fa-star-o {
  color: #ccc;
}

.star-rating i.active {
  color: #FFD700;
}

.star-rating i.hovered {
  color: #FFD700;
  transform: scale(1.2);
}
.star-rating i.hovered {
  color: #FFD700;
  transform: scale(1.2);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: var(--text-color);
}

.form-group textarea {
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
  resize: vertical;
}

.form-group textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.login-to-review {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  text-align: center;
  box-shadow: 0 2px 8px var(--shadow-color);
}

.login-to-review p {
  margin: 0;
  color: var(--text-light);
}

.login-to-review a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}

.login-to-review a:hover {
  text-decoration: underline;
}

/* Reviews List */
.reviews-list {
  margin-top: 2rem;
}

.loading-reviews {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem 0;
}

.no-reviews {
  text-align: center;
  padding: 2rem 0;
  color: var(--text-light);
}

.no-reviews i {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.review-item {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 8px var(--shadow-color);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.1rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: var(--text-color);
}

.review-date {
  font-size: 0.85rem;
  color: var(--text-light);
}

.review-content {
  line-height: 1.6;
  color: var(--text-color);
  margin-bottom: 1rem;
}

.review-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-edit, .btn-delete {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.3rem;
  border: none;
}

.btn-edit {
  background-color: var(--bg-color);
  color: var(--text-color);
}

.btn-edit:hover {
  background-color: var(--border-color);
}

.btn-delete {
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.btn-delete:hover {
  background-color: rgba(244, 67, 54, 0.2);
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

/* Recipe Sidebar */
.recipe-sidebar {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.recipe-info-card, .sidebar-action-card, .similar-recipes-card {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 15px var(--shadow-color);
}

.recipe-info-card h3, .sidebar-action-card h3, .similar-recipes-card h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  font-size: 1.2rem;
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 0.8rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 0.8rem 0;
  border-bottom: 1px solid var(--border-color);
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: var(--text-light);
}

.info-value {
  color: var(--text-color);
}

.sidebar-action-card p {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-light);
}

.full-width {
  width: 100%;
}

.similar-recipes-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.similar-recipe-item {
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 1rem;
}

.similar-recipe-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.similar-recipe-link {
  display: flex;
  gap: 1rem;
  text-decoration: none;
  color: var(--text-color);
}

.similar-recipe-image {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.similar-recipe-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.similar-recipe-info {
  flex: 1;
}

.similar-recipe-info h4 {
  margin: 0 0 0.5rem;
  font-size: 1rem;
  line-height: 1.4;
}

.similar-recipe-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: var(--text-light);
}

.similar-recipe-rating i {
  color: #FFD700;
}

.no-similar-recipes {
  color: var(--text-light);
  font-style: italic;
}

/* Modals */
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
  max-width: 500px;
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

.share-modal {
  max-width: 450px;
}

.shopping-list-modal {
  max-width: 600px;
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

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.warning {
  color: #f44336;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
}

/* Share Modal */
.share-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.share-btn {
  padding: 0.8rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border: none;
  color: white;
}

.share-btn.facebook {
  background-color: #3b5998;
}

.share-btn.twitter {
  background-color: #1da1f2;
}

.share-btn.pinterest {
  background-color: #bd081c;
}

.share-btn.email {
  background-color: #7d7d7d;
}

.share-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.share-link {
  display: flex;
  margin-top: 1.5rem;
}

.share-link input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px 0 0 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.btn-copy {
  padding: 0.8rem 1.2rem;
  border: none;
  border-radius: 0 8px 8px 0;
  background-color: var(--primary-color);
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-copy:hover {
  background-color: var(--primary-dark);
}

/* Shopping List Modal */
.login-required {
  text-align: center;
  padding: 2rem 0;
}

.shopping-list-options {
  margin-bottom: 1.5rem;
}

.option {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.option label {
  margin-left: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

.existing-lists {
  width: 100%;
  margin-top: 0.8rem;
  margin-left: 1.8rem;
}

.existing-lists select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.ingredients-selection h3 {
  margin-top: 1.5rem;
  margin-bottom: 1rem;
  font-size: 1.1rem;
  color: var(--text-color);
  border-bottom: none;
  padding-bottom: 0;
}

.ingredients-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.ingredient-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 0.8rem;
}

.ingredient-checkbox:last-child {
  margin-bottom: 0;
}

.ingredient-checkbox input[type="checkbox"] {
  margin-right: 0.8rem;
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
  justify-content: center;
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

/* Responsive Styles */
@media (max-width: 992px) {
  .recipe-details {
    grid-template-columns: 1fr;
  }

  .recipe-header {
    height: 400px;
  }

  .recipe-header-content h1 {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .recipe-header {
    height: 350px;
  }

  .recipe-header-content {
    padding: 1.5rem;
  }

  .recipe-header-content h1 {
    font-size: 1.8rem;
  }

  .recipe-meta {
    gap: 1rem;
    font-size: 0.85rem;
  }

  .recipe-actions {
    flex-direction: column;
    gap: 0.8rem;
  }

  .btn-favorite, .btn-share {
    width: 100%;
    justify-content: center;
  }

  .recipe-details {
    padding: 1.5rem;
  }

  .review-header {
    flex-direction: column;
    gap: 1rem;
  }

  .share-options {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .recipe-header {
    height: 300px;
  }

  .recipe-header-content {
    padding: 1rem;
  }

  .recipe-header-content h1 {
    font-size: 1.5rem;
  }

  .badge {
    font-size: 0.8rem;
    padding: 0.2rem 0.6rem;
  }

  .recipe-details {
    padding: 1rem;
  }

  .recipe-section h2 {
    font-size: 1.5rem;
  }

  .form-actions, .modal-actions {
    flex-direction: column-reverse;
  }

  .btn-primary, .btn-secondary, .btn-danger {
    width: 100%;
  }

  .review-actions {
    flex-direction: column;
    gap: 0.5rem;
  }

  .btn-edit, .btn-delete {
    width: 100%;
    justify-content: center;
  }

  .similar-recipe-link {
    flex-direction: column;
  }

  .similar-recipe-image {
    width: 100%;
    height: 120px;
  }
}
</style>



