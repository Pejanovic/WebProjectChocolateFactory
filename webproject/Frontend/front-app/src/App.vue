<template>
  <nav>
    <img src="logo.png" alt="Logo" class="nav-logo">
    <h1 class="nav-title">Choco chef</h1>
    <div class="right-side">
      <div class="shop" @click="goToCart">
        <img src="Icons/shopping-cart1.png" alt="shopping-cart" class="nav-icons" v-if="user.userType === 'CUSTOMER'">
        <div class="shoppingNumber" v-if="user.userType === 'CUSTOMER'"><p>{{ shopping.length }}</p></div>
      </div>
      <img src="Icons/user1.png" alt="Logo" class="nav-icons" @click="goToDahsboard" v-if="user.userType !== 'GUEST'">
      <img src="Icons/logout1.png" alt="Logo" class="nav-icons" @click="logout">
    </div>
  </nav>
  <router-view @update-shopping="fetchShoppings"/>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const shopping = ref([]);
const router = useRouter();
const username = sessionStorage.getItem('username');
const user = ref({});

const fetchUser = async (username) => {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
    user.value = response.data;
  } catch (error) {
    console.error("Došlo je do greške prilikom preuzimanja korisnika", error);
  }
};

const fetchShoppings = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/cart/items', {
      withCredentials: true
    });
    shopping.value = response.data;
  } catch (error) {
    console.error('Error fetching shoppings!', error);
  }
};

function goToCart(){
  router.push({ name: 'carts' });
}

const logout = async () => {
  try {
    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/user/logout', {}, { withCredentials: true });
    if (response.status === 200) {
      sessionStorage.clear();
      router.push({ name: 'LoginRegister' });
    }
  } catch (error) {
    console.error('Error logging out!', error);
  }
};

function goToDahsboard(){
  router.push({ name: 'dashboard' });
}

onMounted(() => {
  fetchShoppings();
  fetchUser(username);
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

h1 {
  font-family: 'Roboto', sans-serif;
  color: #EDDEA4 !important;
  margin: 0;
  flex: 1;
  text-align: center; 
}

nav {
  padding: 30px;
  background-color: rgb(150, 75, 0);
  display: flex;
  align-items: center;
}

.nav-logo {
  width: 40px; 
  height: 40px; 
  margin-right: 10px;
}

.nav-title {
  flex: 1;
  text-align: center; 
}

.right-side {
  display: flex;
  align-items: center;
  position: relative;
}

.nav-icons {
  width: 25px; 
  height: 25px;
  margin-right: 13px;
}

.shop {
  position: relative;
}

.shoppingNumber {
  position: absolute;
  top: -12px;
  right: -1px;
  background-color: #EDDEA4;
  font-family: 'Montserrat', sans-serif;
  text-align: center;
  width: 20px;
  height: 20px;
  padding: 2px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  z-index: 1;
}

.shoppingNumber p {
  color: #fff;
  margin: 0;
  font-size: 11px;
}

.nav-icons:hover {
  cursor: pointer;
}


</style>
