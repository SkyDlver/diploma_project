<!-- src/components/layout/AppFooter.vue -->
<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section about">
          <div class="logo">
            <img src="@/assets/logo.svg" alt="Kooking" />
            <h3>Kooking</h3>
          </div>
          <p>Discover delicious recipes from around the world. Cook with confidence and share your culinary creations.</p>
          <div class="social-links">
            <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
            <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
            <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
            <a href="#" aria-label="Pinterest"><i class="fab fa-pinterest-p"></i></a>
          </div>
        </div>

        <div class="footer-section links">
          <h3>Quick Links</h3>
          <ul>
            <li><router-link to="/dashboard">Home</router-link></li>
            <li><router-link to="/recipes">Recipes</router-link></li>
            <li><router-link to="/categories">Categories</router-link></li>
            <li><router-link to="/about">About Us</router-link></li>
            <li><router-link to="/contact">Contact</router-link></li>
          </ul>
        </div>

        <div class="footer-section categories">
          <h3>Categories</h3>
          <ul>
            <li><router-link to="/category/breakfast">Breakfast</router-link></li>
            <li><router-link to="/category/lunch">Lunch</router-link></li>
            <li><router-link to="/category/dinner">Dinner</router-link></li>
            <li><router-link to="/category/desserts">Desserts</router-link></li>
            <li><router-link to="/category/vegetarian">Vegetarian</router-link></li>
          </ul>
        </div>

        <div class="footer-section newsletter">
          <h3>Subscribe to Newsletter</h3>
          <p>Get weekly recipes and cooking tips delivered to your inbox.</p>
          <form @submit.prevent="subscribeNewsletter">
            <input
              type="email"
              placeholder="Your email address"
              v-model="email"
              required
            />
            <button type="submit">Subscribe</button>
          </form>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} Kooking. All rights reserved.</p>
        <div class="footer-bottom-links">
          <router-link to="/privacy">Privacy Policy</router-link>
          <router-link to="/terms">Terms of Service</router-link>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed } from 'vue';

const email = ref('');
const currentYear = computed(() => new Date().getFullYear());

const subscribeNewsletter = () => {
  // Implement newsletter subscription logic
  alert(`Thank you for subscribing with ${email.value}!`);
  email.value = '';
};
</script>

<style scoped>
.app-footer {
  background-color: var(--footer-bg);
  color: #f5f5f5;
  padding: 4rem 0 1rem;
  margin-top: 4rem;
  transition: background-color 0.3s;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.footer-section h3 {
  color: white;
  margin-bottom: 1.5rem;
  position: relative;
  font-size: 1.2rem;
}

.footer-section h3::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -8px;
  width: 40px;
  height: 2px;
  background-color: var(--primary-color);
}

.about .logo {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.about .logo img {
  height: 30px;
  margin-right: 10px;
}

.about .logo h3 {
  margin: 0;
  color: white;
}

.about .logo h3::after {
  display: none;
}

.about p {
  margin-bottom: 1.5rem;
  line-height: 1.6;
  color: #ccc;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-links a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  transition: all 0.3s;
}

.social-links a:hover {
  background-color: var(--primary-color);
  transform: translateY(-3px);
}

.links ul, .categories ul {
  list-style: none;
  padding: 0;
}

.links li, .categories li {
  margin-bottom: 0.8rem;
}

.links a, .categories a {
  color: #ccc;
  text-decoration: none;
  transition: color 0.3s;
  display: inline-block;
}

.links a:hover, .categories a:hover {
  color: var(--primary-color);
  transform: translateX(5px);
}

.newsletter p {
  margin-bottom: 1.5rem;
  color: #ccc;
  line-height: 1.6;
}

.newsletter form {
  display: flex;
  flex-direction: column;
}

.newsletter input {
  padding: 0.8rem 1rem;
  border: none;
  border-radius: 4px;
  margin-bottom: 0.8rem;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.newsletter input::placeholder {
  color: #aaa;
}

.newsletter input:focus {
  outline: none;
  background-color: rgba(255, 255, 255, 0.15);
}

.newsletter button {
  padding: 0.8rem;
  border: none;
  border-radius: 4px;
  background-color: var(--primary-color);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.newsletter button:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.footer-bottom p {
  color: #aaa;
  margin: 0;
}

.footer-bottom-links {
  display: flex;
  gap: 1.5rem;
}

.footer-bottom-links a {
  color: #aaa;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-bottom-links a:hover {
  color: var(--primary-color);
}

@media (max-width: 1200px) {
  .container {
    padding: 0 1.5rem;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 576px) {
  .app-footer {
    padding: 3rem 0 1rem;
  }

  .container {
    padding: 0 1rem;
  }
}
</style>
