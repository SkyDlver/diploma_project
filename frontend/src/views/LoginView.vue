<template>
  <div class="login-view">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <h2>Welcome Back</h2>
          <p class="subtitle">Sign in to continue your culinary journey</p>
        </div>

        <transition name="fade">
          <div v-if="errorMessage" class="error-message" :class="{ 'success-message': isSuccess }">
            <Icon :icon="isSuccess ? 'mdi:check-circle' : 'mdi:alert-circle'" class="message-icon" />
            {{ errorMessage }}
          </div>
        </transition>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="email">Email Address</label>
            <div class="input-wrapper">
              <Icon icon="mdi:email" class="input-icon" />
              <input
                type="email"
                id="email"
                v-model="email"
                required
                placeholder="Enter your email address"
                :disabled="loading"
                @focus="activeField = 'email'"
                @blur="activeField = ''"
                :class="{ 'active': activeField === 'email' }"
              >
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-wrapper">
              <Icon icon="mdi:lock" class="input-icon" />
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                required
                placeholder="Enter your password"
                :disabled="loading"
                @focus="activeField = 'password'"
                @blur="activeField = ''"
                :class="{ 'active': activeField === 'password' }"
              >
              <button
                type="button"
                class="toggle-password"
                @click="showPassword = !showPassword"
                tabindex="-1"
              >
                <Icon :icon="showPassword ? 'mdi:eye-off' : 'mdi:eye'" />
              </button>
            </div>
            <div class="forgot-password">
              <a href="#" @click.prevent="forgotPassword">Forgot Password?</a>
            </div>
          </div>

          <div class="form-group remember-me">
            <div class="checkbox-wrapper">
              <input
                type="checkbox"
                id="remember"
                v-model="rememberMe"
              >
              <label for="remember" class="checkbox-label">Remember me</label>
            </div>
          </div>

          <button type="submit" class="btn btn-primary" :disabled="loading || !isFormValid">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-if="loading">Signing In...</span>
            <span v-else>Sign In</span>
          </button>
        </form>

        <div class="divider">
          <span>or continue with</span>
        </div>

        <div class="social-login">
          <button class="social-btn google" @click="socialLogin('google')">
            <Icon icon="mdi:google" />
            <span>Google</span>
          </button>
          <button class="social-btn facebook" @click="socialLogin('facebook')">
            <Icon icon="mdi:facebook" />
            <span>Facebook</span>
          </button>
        </div>

        <div class="links">
          <p class="register-link">
            Don't have an account?
            <a href="#" @click.prevent="router.push('/register')">Create Account</a>
          </p>
          <p class="home-link">
            <a href="#" @click.prevent="router.push('/')">
              <Icon icon="mdi:home" /> Return to Home
            </a>
          </p>
        </div>
      </div>
    </div>

    <!-- Forgot Password Modal -->
    <div class="modal" v-if="showForgotPasswordModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Reset Password</h3>
          <button @click="showForgotPasswordModal = false" class="close-btn">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div class="modal-body">
          <p>Enter your email address and we'll send you instructions to reset your password.</p>
          <div class="form-group">
            <label for="reset-email">Email Address</label>
            <div class="input-wrapper">
              <Icon icon="mdi:email" class="input-icon" />
              <input
                type="email"
                id="reset-email"
                v-model="resetEmail"
                required
                placeholder="Enter your email address"
              >
            </div>
          </div>
          <div v-if="resetMessage" class="reset-message" :class="{ 'success': resetSuccess }">
            {{ resetMessage }}
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showForgotPasswordModal = false" class="btn btn-outline">Cancel</button>
          <button @click="sendResetLink" class="btn btn-primary" :disabled="resetLoading">
            <span v-if="resetLoading" class="loading-spinner"></span>
            <span v-if="resetLoading">Sending...</span>
            <span v-else>Send Reset Link</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Icon } from '@iconify/vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const isSuccess = ref(false)
const loading = computed(() => authStore.loading)
const activeField = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)

// Forgot password
const showForgotPasswordModal = ref(false)
const resetEmail = ref('')
const resetMessage = ref('')
const resetSuccess = ref(false)
const resetLoading = ref(false)

const isFormValid = computed(() => {
  return email.value.trim() !== '' && password.value.trim() !== '';
})

// Check for registration success message
onMounted(() => {
  console.log("Login component mounted");
  if (route.query.registered === 'true') {
    isSuccess.value = true;
    errorMessage.value = 'Registration successful! Please log in with your new account.';
    const lastEmail = localStorage.getItem('lastRegisteredEmail');
    if (lastEmail) {
      email.value = lastEmail;
    }
  }
  // Check for OAuth2 error message
  if (route.query.error) {
    errorMessage.value = route.query.error;
    isSuccess.value = false;
  }
})

watch([email, password], () => {
  errorMessage.value = '';
  isSuccess.value = false;
});

const handleLogin = async () => {
  console.log("Login button clicked");

  errorMessage.value = '';
  isSuccess.value = false;

  if (!isFormValid.value) {
    errorMessage.value = "Please enter both email and password";
    return;
  }

  try {
    console.log("Attempting to login with email:", email.value);

    // Call the login method from auth store
    const response = await authStore.login({
      email: email.value,
      password: password.value,
      remember: rememberMe.value
    });

    console.log("Login successful, token received:", response.token);

    if (response && response.token) {
      localStorage.setItem('token', response.token);

      if (rememberMe.value) {
        localStorage.setItem('rememberedEmail', email.value);
      } else {
        localStorage.removeItem('rememberedEmail');
      }
    }

    setTimeout(() => {
      router.push('/dashboard');
    }, 300);
  } catch (error) {
    console.error("Login error:", error);

    if (error.response) {
      console.log("Error response:", error.response.data);
      errorMessage.value = error.response.data.message || 'Login failed. Please check your credentials.';
    } else if (error.request) {
      console.log("Error request:", error.request);
      errorMessage.value = 'No response from server. Please try again.';
    } else {
      console.log("Error message:", error.message);
      errorMessage.value = error.message || 'Login failed. Please try again.';
    }
  }
}

const forgotPassword = () => {
  resetEmail.value = email.value; // Pre-fill with current email
  resetMessage.value = '';
  resetSuccess.value = false;
  showForgotPasswordModal.value = true;
}

const sendResetLink = async () => {
  if (!resetEmail.value.trim()) {
    resetMessage.value = 'Please enter your email address';
    resetSuccess.value = false;
    return;
  }

  resetLoading.value = true;

  // try {
  // Simulated API call for password reset
  await new Promise(resolve => setTimeout(resolve, 1500));
  // await authStore.sendPasswordResetEmail(resetEmail.value);

  resetSuccess.value = true;
  resetMessage.value = 'Password reset link sent! (Simulated)';
  setTimeout(() => {
    showForgotPasswordModal.value = false;
  }, 3000);
  // } catch (error) {
  //   resetSuccess.value = false;
  //   resetMessage.value = 'Failed to send reset link. Please try again.';
  // } finally {
  //   resetLoading.value = false;
  // }
}

const socialLogin = (provider) => {
  if (provider === 'google') {
    // Use the correct path to the OAuth2 authorization endpoint
    window.location.href = '/api/auth/google';
  } else {
    errorMessage.value = `${provider.charAt(0).toUpperCase() + provider.slice(1)} login is not implemented yet.`;
  }
}
</script>

<style scoped>
.login-view {
  width: 100%;
  height: 100%;
}

.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  padding: 2rem 1rem;
  overflow: hidden;
  color: #e0e0e0;
}


.login-box {
  background: rgba(25, 25, 25, 0.9);
  padding: 2.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.6);
  width: 100%;
  max-width: 450px;
  margin: 2rem auto;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

h2 {
  color: #ffffff;
  margin-bottom: 0.5rem;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.subtitle {
  color: #bbb;
  font-size: 1rem;
  margin: 0;
}

.login-form {
  margin-bottom: 0rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #ccc;
  font-weight: 500;
  font-size: 0.95rem;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 1.2rem;
  width: 1.2rem;
  height: 1.2rem;
}

input[type="email"],
input[type="password"],
input[type="text"] {
  width: 100%;
  padding: 0.9rem 1rem 0.9rem 2.5rem;
  border: 2px solid #333;
  border-radius: 8px;
  font-size: 1rem;
  background-color: #121212;
  color: #eee;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #4CAF50;
  background-color: #1e1e1e;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.2);
}

input.active {
  border-color: #4CAF50;
  background-color: #1e1e1e;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 0;
  font-size: 1rem;
}

.toggle-password:hover {
  color: #4CAF50;
}

.forgot-password {
  text-align: right;
  margin-top: 0.5rem;
}

.forgot-password a {
  color: #4CAF50;
  font-size: 0.85rem;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password a:hover {
  color: #66bb6a;
  text-decoration: underline;
}

.remember-me {
  margin-bottom: 0.5rem;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
}

.checkbox-wrapper input[type="checkbox"] {
  width: auto;
  margin-right: 10px;
}

.checkbox-label {
  font-size: 0.9rem;
  color: #bbb;
  margin: 0;
}

.btn {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
}

.btn-outline {
  background: transparent;
  border: 1px solid #4CAF50;
  color: #4CAF50;
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.btn:active:not(:disabled) {
  transform: translateY(0);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 1.5rem 0;
  color: #777;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #444;
}

.divider span {
  padding: 0 1rem;
  font-size: 0.9rem;
}

.social-login {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  background-color: #1e1e1e;
  color: #fff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  gap: 8px;
}

.social-btn svg {
  width: 1.1rem;
  height: 1.1rem;
}

.social-btn.google {
  background-color: #1e1e1e;
  border: 1px solid #444;
}

.social-btn.google:hover {
  background-color: #ea4335;
  border-color: #ea4335;
}

.social-btn.facebook {
  background-color: #1e1e1e;
  border: 1px solid #444;
}

.social-btn.facebook:hover {
  background-color: #1877f2;
  border-color: #1877f2;
}

.links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1.5rem;
  text-align: center;
}

.register-link, .home-link {
  color: #aaa;
  font-size: 0.95rem;
  margin: 0;
}

.register-link a, .home-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.register-link a:hover, .home-link a:hover {
  color: #66bb6a;
}

.error-message {
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  text-align: center;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-left: 3px solid #f44336;
}

.message-icon {
  margin-right: 8px;
  font-size: 1.1rem;
  width: 1.1rem;
  height: 1.1rem;
}

.success-message {
  background-color: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  border-left: 3px solid #4CAF50;
}

/* Loading spinner */
.loading-spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  margin-right: 0.5rem;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Modal styles */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background-color: #1e1e1e;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #fff;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  color: #aaa;
  font-size: 1.2rem;
  cursor: pointer;
  transition: color 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #fff;
}

.close-btn svg {
  width: 1.2rem;
  height: 1.2rem;
}

.modal-body {
  padding: 1.5rem;
  color: #ccc;
}

.modal-body p {
  margin-top: 0;
  margin-bottom: 1.5rem;
}

.reset-message {
  margin-top: 1rem;
  padding: 0.8rem;
  border-radius: 8px;
  text-align: center;
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.reset-message.success {
  background-color: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #333;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.modal-footer .btn {
  width: auto;
  padding: 0.7rem 1.5rem;
}

/* Fade transition for internal elements */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Responsive styles */
@media (max-width: 768px) {
  .login-box {
    padding: 2rem;
  }

  h2 {
    font-size: 1.8rem;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 1rem 0.5rem;
  }

  .login-box {
    padding: 1.5rem;
    border-radius: 12px;
    margin: 1rem auto;
  }

  h2 {
    font-size: 1.5rem;
  }

  .subtitle {
    font-size: 0.9rem;
  }

  .form-group {
    margin-bottom: 1.2rem;
  }

  input[type="email"],
  input[type="password"],
  input[type="text"] {
    padding: 0.8rem 1rem 0.8rem 2.3rem;
    font-size: 0.95rem;
  }

  .btn {
    padding: 0.9rem;
  }

  .social-login {
    flex-direction: column;
    gap: 0.8rem;
  }
}

@media (max-width: 360px) {
  .login-box {
    padding: 1.2rem;
  }

  h2 {
    font-size: 1.3rem;
  }

  .subtitle {
    font-size: 0.85rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .input-icon {
    font-size: 0.9rem;
  }

  input[type="email"],
  input[type="password"],
  input[type="text"] {
    padding: 0.7rem 1rem 0.7rem 2.2rem;
    font-size: 0.9rem;
  }

  .btn {
    padding: 0.8rem;
    font-size: 0.95rem;
  }
}
</style>
