<!-- src/views/UserProfileView.vue -->
<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>My Profile</h1>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading your profile...</p>
    </div>

    <div v-else class="profile-content">
      <!-- Profile Information Section -->
      <div class="profile-section">
        <h2>Personal Information</h2>

        <div v-if="!editingProfile" class="profile-info">
          <div class="info-row">
            <div class="info-label">First Name</div>
            <div class="info-value">{{ profile.firstName }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">Last Name</div>
            <div class="info-value">{{ profile.lastName }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">Email</div>
            <div class="info-value">{{ profile.email }}</div>
          </div>

          <button class="btn-primary" @click="startEditingProfile">
            <i class="fas fa-edit"></i> Edit Profile
          </button>
        </div>

        <form v-else class="profile-form" @submit.prevent="updateProfile">
          <div class="form-group">
            <label for="firstName">First Name</label>
            <input
              id="firstName"
              v-model="editedProfile.firstName"
              type="text"
              required
            />
            <span v-if="errors.firstName" class="error-message">{{ errors.firstName }}</span>
          </div>

          <div class="form-group">
            <label for="lastName">Last Name</label>
            <input
              id="lastName"
              v-model="editedProfile.lastName"
              type="text"
              required
            />
            <span v-if="errors.lastName" class="error-message">{{ errors.lastName }}</span>
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input
              id="email"
              v-model="profile.email"
              type="email"
              disabled
            />
            <small>Email cannot be changed</small>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="cancelEditingProfile">
              Cancel
            </button>
            <button type="submit" class="btn-primary">
              Save Changes
            </button>
          </div>
        </form>
      </div>

      <!-- Preferences Section -->
      <div class="profile-section">
        <h2>Cooking Preferences</h2>

        <div v-if="!editingPreferences" class="preferences-info">
          <div class="preferences-group">
            <h3>Preferred Cuisines</h3>
            <div class="tags-container">
              <div v-if="preferences.preferredCuisine && preferences.preferredCuisine.length > 0" class="tags">
                <span v-for="cuisine in preferences.preferredCuisine" :key="cuisine" class="tag">
                  {{ formatEnum(cuisine) }}
                </span>
              </div>
              <div v-else class="empty-preference">No preferred cuisines selected</div>
            </div>
          </div>

          <div class="preferences-group">
            <h3>Preferred Meal Types</h3>
            <div class="tags-container">
              <div v-if="preferences.preferredMealTypes && preferences.preferredMealTypes.length > 0" class="tags">
                <span v-for="mealType in preferences.preferredMealTypes" :key="mealType" class="tag">
                  {{ formatEnum(mealType) }}
                </span>
              </div>
              <div v-else class="empty-preference">No preferred meal types selected</div>
            </div>
          </div>

          <div class="preferences-group">
            <h3>Dietary Restrictions</h3>
            <div class="tags-container">
              <div v-if="preferences.dietaryRestrictions && preferences.dietaryRestrictions.length > 0" class="tags">
                <span v-for="diet in preferences.dietaryRestrictions" :key="diet" class="tag">
                  {{ formatEnum(diet) }}
                </span>
              </div>
              <div v-else class="empty-preference">No dietary restrictions selected</div>
            </div>
          </div>

          <div class="preferences-group">
            <h3>Preferred Cooking Methods</h3>
            <div class="tags-container">
              <div v-if="preferences.preferredCookingMethods && preferences.preferredCookingMethods.length > 0" class="tags">
                <span v-for="method in preferences.preferredCookingMethods" :key="method" class="tag">
                  {{ formatEnum(method) }}
                </span>
              </div>
              <div v-else class="empty-preference">No preferred cooking methods selected</div>
            </div>
          </div>

          <div class="preferences-group">
            <h3>Preferred Difficulty Level</h3>
            <div class="difficulty-level">
              {{ preferences.preferredDifficulty ? formatEnum(preferences.preferredDifficulty) : 'Not specified' }}
            </div>
          </div>

          <button class="btn-primary" @click="startEditingPreferences">
            <i class="fas fa-edit"></i> Edit Preferences
          </button>
        </div>

        <form v-else class="preferences-form" @submit.prevent="updatePreferences">
          <div class="form-group">
            <label>Preferred Cuisines</label>
            <div class="checkboxes-container">
              <div v-for="cuisine in cuisineOptions" :key="cuisine" class="checkbox-item">
                <input
                  :id="`cuisine-${cuisine}`"
                  type="checkbox"
                  :value="cuisine"
                  v-model="editedPreferences.preferredCuisine"
                />
                <label :for="`cuisine-${cuisine}`">{{ formatEnum(cuisine) }}</label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>Preferred Meal Types</label>
            <div class="checkboxes-container">
              <div v-for="mealType in mealTypeOptions" :key="mealType" class="checkbox-item">
                <input
                  :id="`meal-${mealType}`"
                  type="checkbox"
                  :value="mealType"
                  v-model="editedPreferences.preferredMealTypes"
                />
                <label :for="`meal-${mealType}`">{{ formatEnum(mealType) }}</label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>Dietary Restrictions</label>
            <div class="checkboxes-container">
              <div v-for="diet in dietTypeOptions" :key="diet" class="checkbox-item">
                <input
                  :id="`diet-${diet}`"
                  type="checkbox"
                  :value="diet"
                  v-model="editedPreferences.dietaryRestrictions"
                />
                <label :for="`diet-${diet}`">{{ formatEnum(diet) }}</label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>Preferred Cooking Methods</label>
            <div class="checkboxes-container">
              <div v-for="method in cookingMethodOptions" :key="method" class="checkbox-item">
                <input
                  :id="`method-${method}`"
                  type="checkbox"
                  :value="method"
                  v-model="editedPreferences.preferredCookingMethods"
                />
                <label :for="`method-${method}`">{{ formatEnum(method) }}</label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="difficulty">Preferred Difficulty Level</label>
            <select id="difficulty" v-model="editedPreferences.preferredDifficulty">
              <option value="">No preference</option>
              <option v-for="difficulty in difficultyOptions" :key="difficulty" :value="difficulty">
                {{ formatEnum(difficulty) }}
              </option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="cancelEditingPreferences">
              Cancel
            </button>
            <button type="submit" class="btn-primary">
              Save Preferences
            </button>
          </div>
        </form>
      </div>

      <!-- Quick Links Section -->
      <div class="profile-section">
        <h2>Quick Links</h2>
        <div class="quick-links">
          <router-link to="/favorites" class="quick-link">
            <i class="fas fa-heart"></i>
            <span>My Favorite Recipes</span>
          </router-link>
          <router-link to="/my-recipes" class="quick-link">
            <i class="fas fa-book-open"></i>
            <span>My Recipes</span>
          </router-link>
          <router-link to="/shopping-list" class="quick-link">
            <i class="fas fa-shopping-basket"></i>
            <span>Shopping Lists</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  getUserProfile,
  updateUserProfile,
  getUserPreferences,
  updateUserPreferences
} from '@/services/api';

// State
const loading = ref(true);
const profile = ref({
  firstName: '',
  lastName: '',
  email: ''
});
const preferences = ref({
  preferredCuisine: [],
  preferredMealTypes: [],
  dietaryRestrictions: [],
  preferredCookingMethods: [],
  preferredDifficulty: null
});
const editingProfile = ref(false);
const editingPreferences = ref(false);
const editedProfile = reactive({
  firstName: '',
  lastName: ''
});
const editedPreferences = reactive({
  preferredCuisine: [],
  preferredMealTypes: [],
  dietaryRestrictions: [],
  preferredCookingMethods: [],
  preferredDifficulty: null
});
const errors = ref({});

// Enum options
const cuisineOptions = [
  'ITALIAN', 'FRENCH', 'CHINESE', 'JAPANESE', 'MEXICAN',
  'INDIAN', 'THAI', 'MEDITERRANEAN', 'AMERICAN', 'MIDDLE_EASTERN'
];
const mealTypeOptions = [
  'BREAKFAST', 'LUNCH', 'DINNER', 'DESSERT', 'SNACK', 'APPETIZER'
];
const dietTypeOptions = [
  'VEGETARIAN', 'VEGAN', 'GLUTEN_FREE', 'DAIRY_FREE', 'KETO',
  'PALEO', 'LOW_CARB', 'LOW_FAT', 'HIGH_PROTEIN'
];
const cookingMethodOptions = [
  'BAKING', 'GRILLING', 'FRYING', 'BOILING', 'STEAMING',
  'ROASTING', 'SLOW_COOKING', 'PRESSURE_COOKING', 'NO_COOK'
];
const difficultyOptions = [
  'EASY', 'MEDIUM', 'HARD'
];

// Format enum values
const formatEnum = (value) => {
  if (!value) return '';
  return value
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, char => char.toUpperCase());
};

// Fetch user profile
const fetchProfile = async () => {
  try {
    const response = await getUserProfile();
    profile.value = response.data;
  } catch (error) {
    console.error('Error fetching user profile:', error);
  }
};

// Fetch user preferences
const fetchPreferences = async () => {
  try {
    const response = await getUserPreferences();
    preferences.value = response.data;
  } catch (error) {
    console.error('Error fetching user preferences:', error);
  } finally {
    loading.value = false;
  }
};

// Start editing profile
const startEditingProfile = () => {
  editedProfile.firstName = profile.value.firstName;
  editedProfile.lastName = profile.value.lastName;
  errors.value = {};
  editingProfile.value = true;
};

// Cancel editing profile
const cancelEditingProfile = () => {
  editingProfile.value = false;
  errors.value = {};
};

// Update profile
const updateProfile = async () => {
  errors.value = {};

  try {
    const response = await updateUserProfile({
      firstName: editedProfile.firstName,
      lastName: editedProfile.lastName
    });

    profile.value = response.data;
    editingProfile.value = false;
  } catch (error) {
    console.error('Error updating profile:', error);
    if (error.response && error.response.data) {
      errors.value = error.response.data;
    }
  }
};

// Start editing preferences
const startEditingPreferences = () => {
  editedPreferences.preferredCuisine = [...(preferences.value.preferredCuisine || [])];
  editedPreferences.preferredMealTypes = [...(preferences.value.preferredMealTypes || [])];
  editedPreferences.dietaryRestrictions = [...(preferences.value.dietaryRestrictions || [])];
  editedPreferences.preferredCookingMethods = [...(preferences.value.preferredCookingMethods || [])];
  editedPreferences.preferredDifficulty = preferences.value.preferredDifficulty || '';

  editingPreferences.value = true;
};

// Cancel editing preferences
const cancelEditingPreferences = () => {
  editingPreferences.value = false;
};

// Update preferences
const updatePreferences = async () => {
  try {
    const response = await updateUserPreferences(editedPreferences);
    preferences.value = response.data;
    editingPreferences.value = false;
  } catch (error) {
    console.error('Error updating preferences:', error);
  }
};

// Initialize
onMounted(() => {
  fetchProfile();
  fetchPreferences();
});
</script>

<style scoped>
.profile-page {
  max-width: 900px;
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

/* Loading State */
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

/* Profile Sections */
.profile-section {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 15px var(--shadow-color);
}

.profile-section h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 0.8rem;
}

/* Profile Info */
.profile-info {
  margin-bottom: 1.5rem;
}

.info-row {
  display: flex;
  margin-bottom: 1rem;
}

.info-label {
  width: 120px;
  font-weight: 500;
  color: var(--text-light);
}

.info-value {
  flex: 1;
  color: var(--text-color);
}

/* Preferences */
.preferences-group {
  margin-bottom: 1.5rem;
}

.preferences-group h3 {
  font-size: 1rem;
  margin-bottom: 0.8rem;
  color: var(--text-color);
}

.tags-container {
  min-height: 40px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.9rem;
}

.empty-preference {
  color: var(--text-light);
  font-style: italic;
}

.difficulty-level {
  padding: 0.4rem 0.8rem;
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--primary-color);
  border-radius: 4px;
  display: inline-block;
}

/* Forms */
.profile-form, .preferences-form {
  margin-bottom: 1rem;
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

.form-group input[type="text"],
.form-group input[type="email"],
.form-group select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.form-group input[disabled] {
  background-color: rgba(0, 0, 0, 0.05);
  cursor: not-allowed;
}

.form-group small {
  display: block;
  margin-top: 0.5rem;
  color: var(--text-light);
  font-size: 0.85rem;
}

.error-message {
  color: #f44336;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  display: block;
}

.checkboxes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 0.8rem;
  max-height: 200px;
  overflow-y: auto;
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
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
  margin-top: 1.5rem;
}

/* Quick Links */
.quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.quick-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  border-radius: 8px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  text-decoration: none;
  color: var(--text-color);
  transition: all 0.3s;
}

.quick-link:hover {
  background-color: var(--border-color);
  transform: translateY(-3px);
}

.quick-link i {
  font-size: 1.5rem;
  margin-bottom: 0.8rem;
  color: var(--primary-color);
}

/* Button Styles */
.btn-primary, .btn-secondary {
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

/* Responsive Styles */
@media (max-width: 768px) {
  .profile-page {
    padding: 1rem;
  }

  .profile-section {
    padding: 1.5rem;
  }

  .info-row {
    flex-direction: column;
  }

  .info-label {
    width: 100%;
    margin-bottom: 0.3rem;
  }

  .checkboxes-container {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn-primary, .btn-secondary {
    width: 100%;
    justify-content: center;
  }

  .quick-links {
    grid-template-columns: 1fr;
  }
}
</style>
