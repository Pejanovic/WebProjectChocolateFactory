<template>
  <div>
    <div class="error-div">
      <p class="choco-error">{{ error }}</p>
    </div>
    <div class="chocolate-list">
      <div v-for="choco in chocolates" :key="choco.id" class="chocolate-item" @click="updateChocolate(choco)">
        <img src="download.jpeg" alt="Chocolate Image" class="image">
        <h3>{{ choco.name }}</h3>
        <div class="choco-info">
          <p class="choco-detail"><img src="Icons/dollar.png" class="icon"> {{ choco.price }} din</p>
          <p class="choco-detail"><img src="Icons/weight.png" class="icon"> {{ choco.grammage }}g</p>
        </div>
        <p class="choco-desc">Description: {{ choco.description }}</p>
        <div class="footer-status">
          <p>{{ choco.status }}</p>
          <p>{{ choco.category }} - {{ choco.type }}</p>
        </div>
        <div class="amount-selector" @click.stop>
          <button @click="decrementQuantity(choco.id)" class="amount-button-left">-</button>
          <input type="text" :value="choco.quantity || 1" class="amount-input" readonly>
          <button @click="incrementQuantity(choco.id)" class="amount-button-right">+</button>
        </div>
      </div>
    </div>
  </div>
  </template>
  
  <script setup>
  import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
  
  const chocolates = ref([]);
  const route = useRoute();
  const type = sessionStorage.getItem('userType');
  const username = sessionStorage.getItem('username');
  const user = ref(null);
  
  const error = ref("");
  
  const fetchUser = async (username) => {
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
      user.value = response.data;
      fetchChocolates(user.value.factoryId);
    } catch (error) {
      console.error("Došlo je do greške prilikom preuzimanja korisnika", error);
    }
  };
  
  const fetchChocolates = (factoryId) => {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/FactorySearch?id=${factoryId}`)
      .then(response => {
        chocolates.value = response.data;
        console.log(chocolates);
      })
      .catch(error => {
        console.error('There was an error fetching the chocolates!', error);
      });
  };
  
  const updateChocolate = (choco) => {
    axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${choco.id}`, choco)
      .then(response => {
        console.log('Chocolate quantity updated successfully', response);
        error.value = "";
      })
      .catch(err => {
        if (err.response && err.response.data) {
          error.value = err.response.data; 
        } else {
          error.value = "There was an error updating the chocolate quantity.";
        }
        console.error('There was an error updating the chocolate quantity', err);
      });
  };
  
  const incrementQuantity = (id) => {
    const chocolate = chocolates.value.find(choco => choco.id === id);
    if (chocolate) {
      chocolate.quantity += 1;
    }
  };
  
  const decrementQuantity = (id) => {
    const chocolate = chocolates.value.find(choco => choco.id === id);
    if (chocolate && chocolate.quantity > 1) {
      chocolate.quantity -= 1;
    }
  };
  
  onMounted(() => {
    fetchUser(username);
  });
  </script>
  
  <style>
  @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
  
  .error-div {
    text-align: center;
  }
  .choco-error {
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    color: red;
  }
  
  .chocolate-list {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    margin: 10px;
  }
  
  .chocolate-item {
    border: 1px solid black;
    border-radius: 10%;
    margin: 10px;
    background-color: transparent;
    width: 240px;
    height: 300px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    padding: 10px;
    transition: box-shadow 0.3s ease;
  }
  
  .chocolate-item:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    cursor: pointer;
  }
  
  .chocolate-item h3 {
    font-family: 'Montserrat', sans-serif;
    text-align: center;
    margin-bottom: 5%;
  }
  
  .image {
    width: calc(100% + 20px);
    height: 35%;
    object-fit: cover;
    display: block;
    margin: -10px;
  }
  
  .choco-info {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .choco-detail {
    display: flex;
    align-items: center;
    font-family: 'Montserrat', sans-serif;
    font-size: 12px;
    margin: 10px;
  }
  
  .icon {
    margin-right: 5px;
    width: 25px;
    height: 25px;
  }
  
  .choco-desc {
    height: 90px;
    margin: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    font-family: 'Montserrat', sans-serif;
    font-size: 12px;
  }
  
  .amount-selector {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10px;
  }
  
  .amount-input {
    width: 40px;
    height: 20px;
    text-align: center;
    font-size: 16px;
    border: 1px solid #ddd;
    font-family: 'Montserrat', sans-serif;
  }
  
  .amount-button-left, .amount-button-right {
    background-color: #EDDEA4;
    color: white;
    border: none;
    width: 30px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 18px;
  }
  
  .amount-button-left {
    border-top-left-radius: 5px;
    border-bottom-left-radius: 5px;
  }
  
  .amount-button-right {
    border-top-right-radius: 5px;
    border-bottom-right-radius: 5px;
  }
  
  .amount-button-left:hover, .amount-button-right:hover {
    background-color: #D4C48F;
  }
  
  .footer-status {
    display: flex;
    justify-content: space-around;
    font-family: 'Montserrat', sans-serif;
    font-size: 10px;
    font-weight: bold;
    margin-bottom: 12px;
  }

  .footer-comment{
    display: flex;
    justify-content: center;
    font-family: 'Montserrat', sans-serif;
    font-size: 10px;
    margin: 0;
  }
  
  .update-button {
    margin-top: 10px;
    padding: 5px 10px;
    font-family: 'Montserrat', sans-serif;
    background-color: #EDDEA4;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .update-button:hover {
    background-color: #D4C48F;
  }
  </style>
  