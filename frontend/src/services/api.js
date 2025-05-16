import axios from "axios";

const apiClient = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for handling common errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle 401 Unauthorized errors
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      // Redirect to login if needed
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
// Auth services
export const loginUser = async (email, password) => {
  console.log("API: Logging in user with email:", email);

  try {
    const response = await apiClient.post('/auth/login', {
      email,
      password
    });
    console.log("API: Login successful");
    return response;
  } catch (error) {
    console.error("API: Login failed:", error);
    throw error;
  }
};

export const registerUser = (userData) => {
  return apiClient.post('/auth/register', userData);
};

export const getTrendingRecipes = () => {
  return apiClient.get('/recipes/trending');
};

export const getRecommendedRecipes = () => {
  return apiClient.get('/recipes/recommended');
};

export const getSeasonalRecipes = () => {
  return apiClient.get('/recipes/seasonal');
};

export const getRecipeById = (id) => {
  return apiClient.get(`/recipes/${id}`);
};

export const getRecipeBrief = (id) => {
  return apiClient.get(`/recipes/${id}/brief`);
};

export const searchRecipes = (searchParams) => {
  return apiClient.get('/recipes', { params: searchParams });
};

export const advancedSearchRecipes = (params) => {
  return apiClient.get('/recipes/advanced-search', { params });
};

export const favoriteRecipe = (id) => {
  return apiClient.post(`/recipes/${id}/favorite`);
};

export const unfavoriteRecipe = (id) => {
  return apiClient.delete(`/recipes/${id}/favorite`);


};

//  Shopping list endpoints
export const getUserShoppingLists = () => {
  return apiClient.get('/shopping-lists/user');
};

export const getShoppingListById = (id) => {
  return apiClient.get(`/shopping-lists/${id}`);
};

export const createShoppingList = (data) => {
  return apiClient.post('/shopping-lists', data);
};

export const updateShoppingList = (id, data) => {
  return apiClient.put(`/shopping-lists/${id}`, data);
};

export const updateShoppingListStatus = (id, data) => {
  return apiClient.put(`/shopping-lists/${id}/status`, data);
};

export const deleteShoppingList = (id) => {
  return apiClient.delete(`/shopping-lists/${id}`);
};

export const addIngredientsToShoppingList = (id, data) => {
  return apiClient.post(`/shopping-lists/${id}/ingredients`, data);
};

export const removeIngredientFromShoppingList = (id, ingredientId) => {
  return apiClient.delete(`/shopping-lists/${id}/ingredients/${ingredientId}`);
};
// User Recipes endpoints
export const getUserRecipes = (page = 0, size = 12, search = '', cuisine = '', mealType = '', sort = 'newest') => {
  const params = { page, size };
  if (search) params.search = search;
  if (cuisine) params.cuisine = cuisine;
  if (mealType) params.mealType = mealType;
  if (sort) params.sort = sort;

  return apiClient.get('/recipes/user', { params });
};

// Recipe CRUD operations

export const createRecipe = (recipeData) => {
  return apiClient.post('/recipes', recipeData);
};

export const updateRecipe = (id, recipeData) => {
  return apiClient.put(`/recipes/${id}`, recipeData);
};

export const deleteRecipe = (id) => {
  return apiClient.delete(`/recipes/${id}`);
};
// Ingredient endpoints
export const getAllIngredients = (page = 0, size = 20, search = '', category = '') => {
  const params = { page, size };
  if (search) params.search = search;
  if (category) params.category = category;

  return apiClient.get('/ingredients', { params });
};

export const getIngredientById = (id) => {
  return apiClient.get(`/ingredients/${id}`);
};

export const getAllCategories = () => {
  return apiClient.get('/ingredients/categories');
};

export const createIngredient = (ingredientData) => {
  return apiClient.post('/ingredients', ingredientData);
};

export const updateIngredient = (id, ingredientData) => {
  return apiClient.put(`/ingredients/${id}`, ingredientData);
};

export const deleteIngredient = (id) => {
  return apiClient.delete(`/ingredients/${id}`);
};

export const addSubstitute = (id, substituteId) => {
  return apiClient.post(`/ingredients/${id}/substitutes/${substituteId}`);
};

export const removeSubstitute = (id, substituteId) => {
  return apiClient.delete(`/ingredients/${id}/substitutes/${substituteId}`);
};

// User profile endpoints
export const getUserProfile = () => {
  return apiClient.get('/users/me');
};

export const updateUserProfile = (profileData) => {
  return apiClient.put('/users/me', profileData);
};

export const getUserPreferences = () => {
  return apiClient.get('/users/me/preferences');
};

export const updateUserPreferences = (preferencesData) => {
  return apiClient.put('/users/me/preferences', preferencesData);
};

export const getUserFavoriteRecipes = (page = 0, size = 12) => {
  return apiClient.get('/users/me/favorite-recipes', {
    params: { page, size }
  });
};
// Review API functions
export const getRecipeReviews = (recipeId, page = 0, size = 10) => {
  return apiClient.get(`/reviews/recipe/${recipeId}`, {
    params: { page, size }
  });
};

export const createReview = (recipeId, reviewData) => {
  return apiClient.post(`/reviews/recipe/${recipeId}`, reviewData);
};

export const updateReview = (reviewId, reviewData) => {
  return apiClient.put(`/reviews/${reviewId}`, reviewData);
};

export const deleteReview = (reviewId) => {
  return apiClient.delete(`/reviews/${reviewId}`);
};

export const getCurrentUser = async () => {
  console.log("API: Fetching current user");
  try {
    const response = await apiClient.get('/users/me');
    console.log("API: Current user data", response.data);
    return response;
  } catch (error) {
    console.error("API: Error fetching current user", error);
    throw error;
  }
};
