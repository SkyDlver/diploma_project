<template>
  <div>
    <div class="register-container">
      <div class="register-box">
        <div class="register-header">
          <h2>Create Your Account</h2>
          <p class="subtitle">Join our cooking community and start your culinary journey</p>
        </div>

        <transition name="fade">
          <div v-if="errorMessage" class="error-message">
            <Icon icon="mdi:alert-circle" class="error-icon" />
            {{ errorMessage }}
          </div>
        </transition>

        <form @submit.prevent="handleRegister" class="register-form">
          <!-- Side-by-side first name and last name -->
          <div class="name-group">
            <div class="form-group">
              <label for="firstName">First Name</label>
              <div class="input-wrapper">
                <Icon icon="mdi:account" class="input-icon" />
                <input
                  type="text"
                  id="firstName"
                  v-model="firstName"
                  required
                  placeholder="Enter your first name"
                  :disabled="loading"
                  @focus="activeField = 'firstName'"
                  @blur="activeField = ''"
                  :class="{ 'active': activeField === 'firstName' }"
                >
              </div>
            </div>
            <div class="form-group">
              <label for="lastName">Last Name</label>
              <div class="input-wrapper">
                <Icon icon="mdi:account" class="input-icon" />
                <input
                  type="text"
                  id="lastName"
                  v-model="lastName"
                  required
                  placeholder="Enter your last name"
                  :disabled="loading"
                  @focus="activeField = 'lastName'"
                  @blur="activeField = ''"
                  :class="{ 'active': activeField === 'lastName' }"
                >
              </div>
            </div>
          </div>

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
              <Icon icon="mdi:lock-open" class="input-icon" />
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                required
                placeholder="Create a strong password"
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
            <div class="password-strength" v-if="password">
              <div class="strength-meter">
                <div
                  class="strength-bar"
                  :style="{ width: passwordStrength.score * 25 + '%' }"
                  :class="passwordStrengthClass"
                ></div>
              </div>
              <span class="strength-text" :class="passwordStrengthClass">
                {{ passwordStrength.message }}
              </span>
            </div>
            <small class="password-hint">Password must be at least 8 characters with letters and numbers</small>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <div class="input-wrapper">
              <Icon icon="mdi:lock" class="input-icon" />
              <input
                :type="showPassword ? 'text' : 'password'"
                id="confirmPassword"
                v-model="confirmPassword"
                required
                placeholder="Confirm your password"
                :disabled="loading"
                @focus="activeField = 'confirmPassword'"
                @blur="activeField = ''"
                :class="{
                  'active': activeField === 'confirmPassword',
                  'input-match': confirmPassword && password === confirmPassword,
                  'input-error': confirmPassword && password !== confirmPassword
                }"
              >
              <span class="password-match" v-if="confirmPassword">
                <Icon
                  :icon="password === confirmPassword ? 'mdi:check-circle' : 'mdi:close-circle'"
                  :style="{ color: password === confirmPassword ? '#4CAF50' : '#f44336' }"
                />
              </span>
            </div>
            <small class="password-hint error" v-if="confirmPassword && password !== confirmPassword">
              Passwords do not match
            </small>
          </div>

          <div class="form-group terms-group">
            <div class="checkbox-wrapper">
              <input
                type="checkbox"
                id="terms"
                v-model="agreeToTerms"
                required
              >
              <label for="terms" class="checkbox-label">
                I agree to the <a href="#" @click.prevent="showTerms = true">Terms of Service</a> and <a href="#" @click.prevent="showPrivacy = true">Privacy Policy</a>
              </label>
            </div>
          </div>


          <button type="submit" class="btn btn-primary" :disabled="!isFormValid || loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-if="loading">Creating Account...</span>
            <span v-else>Create Account</span>
          </button>
        </form>

        <div class="links">
          <p class="login-link">
            Already have an account?
            <a href="#" @click.prevent="router.push('/login')">Sign In</a>
          </p>
          <p class="home-link">
            <a href="#" @click.prevent="router.push('/')">
              <Icon icon="mdi:home" /> Return to Home
            </a>
          </p>
        </div>
      </div>
    </div>
  </div>

  <!-- Terms Modal -->
  <div class="modal" v-if="showTerms">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Terms of Service</h3>
        <button @click="showTerms = false" class="close-btn">
          <Icon icon="mdi:close" />
        </button>
      </div>
      <div class="modal-body">
        <p>These Terms of Service ("Terms") govern your access to and use of our website, services, and applications (the "Services"). By accessing or using the Services, you agree to be bound by these Terms.</p>
        <h4>1. Account Registration</h4>
        <p>When you create an account with us, you must provide accurate and complete information. You are responsible for safeguarding your account, and you agree to notify us immediately of any unauthorized use of your account.</p>
        <h4>2. User Content</h4>
        <p>You retain ownership of any content you submit, post, or display on or through the Services. By submitting content, you grant us a worldwide, non-exclusive, royalty-free license to use, copy, reproduce, process, adapt, modify, publish, transmit, display, and distribute such content.</p>
        <!-- Add more terms as needed -->
      </div>
      <div class="modal-footer">
        <button @click="showTerms = false" class="btn btn-primary">I Understand</button>
      </div>
    </div>
  </div>

  <!-- Privacy Modal -->
  <div class="modal" v-if="showPrivacy">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Privacy Policy</h3>
        <button @click="showPrivacy = false" class="close-btn">
          <Icon icon="mdi:close" />
        </button>
      </div>
      <div class="modal-body">
        <p>This Privacy Policy describes how we collect, use, and disclose your information when you use our Services.</p>
        <h4>1. Information We Collect</h4>
        <p>We collect information you provide directly to us when you create an account, update your profile, or interact with features of our Services. This may include your name, email address, and any other information you choose to provide.</p>
        <h4>2. How We Use Your Information</h4>
        <p>We use the information we collect to provide, maintain, and improve our Services, to develop new features, and to protect us and our users.</p>
        <!-- Add more privacy details as needed -->
      </div>
      <div class="modal-footer">
        <button @click="showPrivacy = false" class="btn btn-primary">I Understand</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Icon } from '@iconify/vue'
const router = useRouter()
const authStore = useAuthStore()

const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const loading = computed(() => authStore.loading)
const activeField = ref('')
const showPassword = ref(false)
const agreeToTerms = ref(false)
const showTerms = ref(false)
const showPrivacy = ref(false)


// For debugging
onMounted(() => {
  console.log("Register component mounted");
})

// Password strength calculation
const passwordStrength = computed(() => {
  if (!password.value) return { score: 0, message: '' };

  let score = 0;
  let message = 'Weak';

  // Length check
  if (password.value.length >= 8) score += 1;
  if (password.value.length >= 12) score += 1;

  // Complexity checks
  if (/[A-Z]/.test(password.value)) score += 1;
  if (/[a-z]/.test(password.value)) score += 1;
  if (/[0-9]/.test(password.value)) score += 1;
  if (/[^A-Za-z0-9]/.test(password.value)) score += 1;

  // Cap at 4
  score = Math.min(score, 4);

  // Set message based on score
  if (score === 1) message = 'Weak';
  else if (score === 2) message = 'Fair';
  else if (score === 3) message = 'Good';
  else if (score === 4) message = 'Strong';

  return { score, message };
});

const passwordStrengthClass = computed(() => {
  const score = passwordStrength.value.score;
  if (score === 0 || score === 1) return 'strength-weak';
  if (score === 2) return 'strength-fair';
  if (score === 3) return 'strength-good';
  return 'strength-strong';
});


const isFormValid = computed(() => {
  return firstName.value.trim() !== '' &&
    lastName.value.trim() !== '' &&
    email.value.trim() !== '' &&
    password.value.length >= 8 &&
    password.value === confirmPassword.value &&
    agreeToTerms.value;
});

// Clear error message when form fields change
watch([firstName, lastName, email, password, confirmPassword], () => {
  errorMessage.value = '';
});

const handleRegister = async () => {
  console.log("Register button clicked");

  // Client-side validation
  if (password.value !== confirmPassword.value) {
    errorMessage.value = "Passwords do not match";
    return;
  }

  if (password.value.length < 8) {
    errorMessage.value = "Password must be at least 8 characters long";
    return;
  }

  if (!isFormValid.value) {
    errorMessage.value = "Please fill out all fields correctly";
    return;
  }

  try {
    console.log("Attempting to register with:", {
      firstName: firstName.value,
      lastName: lastName.value,
      email: email.value,
      password: "********",
    });

    errorMessage.value = '';
    const userData = {
      firstName: firstName.value,
      lastName: lastName.value,
      email: email.value,
      password: password.value,
      confirmPassword: confirmPassword.value
    };

    await authStore.register(userData);
    console.log("Registration successful");
    router.push('/login?registered=true');
  } catch (error) {
    console.error("Registration error:", error);
    if (error.response) {
      console.log("Error response:", error.response.data);
      errorMessage.value = error.response.data.message || 'Registration failed';
    } else if (error.request) {
      console.log("Error request:", error.request);
      errorMessage.value = 'No response from server. Please try again.';
    } else {
      console.log("Error message:", error.message);
      errorMessage.value = error.message || 'Registration failed';
    }
  }
}
</script>

<style scoped>

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

.error-icon {
  margin-right: 8px;
  font-size: 1.1rem;
  width: 1.1rem;
  height: 1.1rem;
  color: #f44336;
}

.register-container {
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

.register-box {
  background: rgba(25, 25, 25, 0.9);
  padding: 2.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.6);
  width: 100%;
  max-width: 550px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.register-header {
  text-align: center;
  margin-bottom: 1.5rem;
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

.register-form {
  margin-bottom:0.5rem;
}

/* Group first and last name side by side */
.name-group {
  display: flex;
  gap: 1rem;

}

.form-group {
  margin-bottom: 1rem;
  flex: 1;
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
  font-size: 1rem;
}

input[type="text"],
input[type="email"],
input[type="password"] {
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

input.input-match {
  border-color: #4CAF50;
}

input.input-error {
  border-color: #f44336;
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

.password-match {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1rem;
}

.password-strength {
  margin-top: 0.5rem;
}

.strength-meter {
  height: 4px;
  background-color: #333;
  border-radius: 2px;
  margin-bottom: 0.3rem;
}

.strength-bar {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.strength-weak { background-color: #f44336; color: #ffffff; }
.strength-fair { background-color: #ff9800; color: #ffffff; }
.strength-good { background-color: #2196F3; color: #ffffff; }
.strength-strong { background-color: #4CAF50; color: #ffffff; }

.strength-text {
  font-size: 0.8rem;
  font-weight: 500;
}

.password-hint {
  display: block;
  color: #777;
  font-size: 0.8rem;
  margin-top: 0.5rem;
}

.password-hint.error {
  color: #f44336;
}

.terms-group {
  margin-top: 1rem;
}

.checkbox-wrapper {
  display: flex;
  align-items: flex-start;
  position: relative;
}

.checkbox-wrapper input[type="checkbox"] {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  width: 18px;
  height: 18px;
  border: 2px solid #555;
  border-radius: 4px;
  background-color: #2c2f33;
  margin-right: 10px;
  margin-top: 2px;
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
}

.checkbox-wrapper input[type="checkbox"]:checked {
  background-color: #4CAF50;
  border-color: #4CAF50;
}

.checkbox-wrapper input[type="checkbox"]::after {
  content: "";
  position: absolute;
  display: none;
  left: 5px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-wrapper input[type="checkbox"]:checked::after {
  display: block;
}

.checkbox-label {
  font-size: 0.9rem;
  color: #bbb;
  margin: 0;
  line-height: 1.4;
}

.checkbox-label a {
  color: #4CAF50;
  text-decoration: none;
}

.checkbox-label a:hover {
  text-decoration: underline;
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
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
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

.links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1.5rem;
  text-align: center;
}

.login-link, .home-link {
  color: #aaa;
  font-size: 0.95rem;
  margin: 0;
}

.login-link a, .home-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.login-link a:hover, .home-link a:hover {
  color: #66bb6a;
}

.home-link a i {
  margin-right: 5px;
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

.error-message i {
  margin-right: 8px;
  font-size: 1.1rem;
}

.validation-message {
  color: #ff9800;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  text-align: center;
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
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
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
}

.close-btn:hover {
  color: #fff;
}

.modal-body {
  padding: 1.5rem;
  color: #ccc;
  line-height: 1.6;
}

.modal-body h4 {
  color: #4CAF50;
  margin: 1.5rem 0 0.5rem;
}

.modal-body p {
  margin: 0 0 1rem;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #333;
  text-align: right;
}

.modal-footer .btn {
  margin: 0;
  width: auto;
  padding: 0.7rem 1.5rem;
}

/* Fade transition
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
*/
/* Responsive adjustments */
@media (max-width: 768px) {
  .register-box {
    padding: 2rem;
  }

  h2 {
    font-size: 1.8rem;
  }
}

@media (max-width: 576px) {
  .name-group {
    flex-direction: column;
    gap: 0;
  }

  .register-box {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  h2 {
    font-size: 1.5rem;
  }

  .subtitle {
    font-size: 0.9rem;
  }

  input[type="text"],
  input[type="email"],
  input[type="password"] {
    padding: 0.8rem 1rem 0.8rem 2.3rem;
    font-size: 0.95rem;
  }

  .btn {
    padding: 0.9rem;
  }
}

@media (max-width: 360px) {
  .register-box {
    padding: 1.2rem;
  }

  h2 {
    font-size: 1.3rem;
  }

  .form-group {
    margin-bottom: 1.2rem;
  }

  .input-icon {
    font-size: 0.9rem;
  }

  input[type="text"],
  input[type="email"],
  input[type="password"] {
    padding: 0.7rem 1rem 0.7rem 2.2rem;
    font-size: 0.9rem;
  }
}
</style>
