<template>
  <div class="shoppings-component">
    <div class="filter-comp">
      <div class="sort-div">
        <div class="ascending-div">
          <p style="margin-bottom: 5px;">Ascending</p>
          <label>
            <input type="radio" v-model="sortKey" value="factoryName-asc">
            Sort by factory name
          </label>
          <label>
            <input type="radio" v-model="sortKey" value="price-asc">
            Sort by price
          </label>
          <label>
            <input type="radio" v-model="sortKey" value="date-asc">
            Sort by date
          </label>
        </div>
        <div class="descending-div">
          <p style="margin-bottom: 5px;">Descending</p>
          <label>
            <input type="radio" v-model="sortKey" value="factoryName-desc">
            Sort by factory name
          </label>
          <label>
            <input type="radio" v-model="sortKey" value="price-desc">
            Sort by price
          </label>
          <label>
            <input type="radio" v-model="sortKey" value="date-desc">
            Sort by date
          </label>
        </div>
      </div>
      <div class="filter-div1">
        <div class="price-div">
          <p>Filter by price</p>
          <div>
            <p>From:</p>
            <input type="number" placeholder="from" v-model="priceFilter.from">
          </div>
          <div>
            <p>To:</p>
            <input type="number" placeholder="to" v-model="priceFilter.to">
          </div>
        </div>
        <div class="separator">|</div>
        <div class="date-div">
          <p>Filter by date</p>
          <div>
            <p>From:</p>
            <input type="date" placeholder="from" v-model="dateFilter.from">
          </div>
          <div>
            <p>To:</p>
            <input type="date" placeholder="to" v-model="dateFilter.to">
          </div>
        </div>
      </div>
    </div>
    <br>
    <div v-if="showRejectForm" class="comment-form-div">
      <h2>Update Shopping Comment</h2>
      <form @submit.prevent="rejectShopping">
        <div class="form-group">
          <label for="idShopping">Shopping ID:</label>
          <input type="text" id="idShopping" v-model="currentShopping.idShopping" readonly>
        </div>
        <div class="form-group">
          <label for="factoryId">Factory ID:</label>
          <input type="text" id="factoryId" v-model="currentShopping.factoryId" readonly>
        </div>
        <div class="form-group">
          <label for="date">Date:</label>
          <input type="text" id="date" v-model="formattedDate" readonly>
        </div>
        <div class="form-group">
          <label for="price">Price:</label>
          <input type="text" id="price" v-model="currentShopping.price" readonly>
        </div>
        <div class="form-group">
          <label for="nameUser">User:</label>
          <input type="text" id="nameUser" v-model="currentShopping.nameUser" readonly>
        </div>
        <div class="form-group">
          <label for="status">Status:</label>
          <input type="text" id="status" v-model="currentShopping.status" readonly>
        </div>
        <div class="form-group">
          <label for="comment">Comment:</label>
          <textarea id="comment" v-model="currentShopping.comment"></textarea>
        </div>
        <button type="submit">Update Comment</button>
      </form>
    </div>
    <br>
    <div class="shoppings-list">
      <div v-for="shopp in filteredShoppings" :key="shopp.id" class="shopp-item">
        <div class="info-container">
          <div class="basic-info">
            <div>
                <img src="Icons/user2.png" width="25px" alt="factory">
                <p>{{ shopp.nameUser }}</p>
            </div>
            <div>
              <img src="Icons/dollar.png" width="25px" alt="price">
              <p>{{ shopp.price }}</p>
            </div>
            <div>
              <img src="Icons/factory.png" width="25px" alt="factory">
              <p>{{ shopp.factoryName }}</p>
            </div>
          </div>
          <div class='name-user-div'>
              <p>Purchase completed:</p>
              <p>{{ formatDate(shopp.date) }}</p>
            </div>
          <div class="status-info">
            <div>
              <p>Status: </p>
              <p> {{ shopp.status }}</p>
            </div>
            <div class="buttons" v-if="shopp.status === 'PROCESSING'">
                <button @click="approveShopping(shopp)" style="color: green">Approve</button>
                <button @click="openRejectForm(shopp)">Reject</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';

const shoppings = ref([]);
const searchName = ref('');
const user = ref([]);
const customer = ref(null);
const username = sessionStorage.getItem('username');
const sortKey = ref('factoryName-asc');
const priceFilter = ref({ from: null, to: null });
const dateFilter = ref({ from: null, to: null });

const showRejectForm = ref(false);
const currentShopping = ref(null);
const formattedDate = ref('');

const fetchUser = () => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true })
    .then(response => {
      user.value = response.data;
      fetchShoppings();
    })
    .catch(error => {
      console.log("Error: ", error);
    });
};

const fetchShoppings = () => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppings/factory?factoryId=${user.value.factoryId}`)
    .then(response => {
      const shoppingPromises = response.data.map(shopp => {
        return axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactories/${shopp.factoryId}`)
          .then(factResponse => {
            shopp.factoryName = factResponse.data.name;
            return shopp;
          });
      });

      Promise.all(shoppingPromises)
        .then(results => {
          shoppings.value = results;
        })
        .catch(error => {
          console.log("error fetching factory names: ", error);
        });
    })
    .catch(error => {
      console.log("error fetching shoppings: ", error);
    });
};

const approveShopping = (shopp) => {
  shopp.status = 'APPROVED';
  axios.put('http://localhost:8080/WebShopAppREST/rest/shoppings/status', shopp)
    .then(() => {
      axios.put(`http://localhost:8080/WebShopAppREST/rest/user/updateUserPoints?username=${shopp.nameUser}&price=${shopp.price}`);
      alert('Shopping approved successfully!');
      fetchShoppings();
    })
    .catch(error => {
      console.error('Error approving shopping:', error);
    });
};

const openRejectForm = (shopping) => {
  currentShopping.value = shopping;
  formattedDate.value = new Date(shopping.date).toLocaleDateString();
  showRejectForm.value = true;
};

const rejectShopping = () => {
  if (!currentShopping.value) return;
  currentShopping.value.status = 'REJECTED';

  axios.put('http://localhost:8080/WebShopAppREST/rest/shoppings/status-comment', currentShopping.value)
    .then(() => {
      alert('Shopping rejected successfully!');
      closeRejectForm();
      fetchShoppings();
    })
    .catch(error => {
      console.error('Error rejecting shopping:', error);
    });
};

const closeRejectForm = () => {
  showRejectForm.value = false;
  currentShopping.value = null;
};

const formatDate = (dateString) => {
  return dateString.split('T')[0];
};

const filteredShoppings = computed(() => {
  let sortedShoppings = [...shoppings.value];

  if (sortKey.value.includes('price')) {
    sortedShoppings.sort((a, b) => {
      return sortKey.value.includes('asc') 
        ? a.price - b.price 
        : b.price - a.price;
    });
  } else if (sortKey.value.includes('factoryName')){
    sortedShoppings.sort((a, b) => {
      if (!a.factoryName) return 1;
      if (!b.factoryName) return -1;
      return sortKey.value.includes('asc') 
        ? a.factoryName.localeCompare(b.factoryName) 
        : b.factoryName.localeCompare(a.factoryName);
    });
  } else if (sortKey.value.includes('date')) {
    sortedShoppings.sort((a, b) => {
      return sortKey.value.includes('asc') 
        ? new Date(a.date) - new Date(b.date) 
        : new Date(b.date) - new Date(a.date);
    });
  }

  return sortedShoppings.filter(shopp => {
    const matchesPrice = (!priceFilter.value.from || shopp.price >= priceFilter.value.from) &&
                         (!priceFilter.value.to || shopp.price <= priceFilter.value.to);
    const matchesDate = (!dateFilter.value.from || new Date(shopp.date) >= new Date(dateFilter.value.from)) &&
                        (!dateFilter.value.to || new Date(shopp.date) <= new Date(dateFilter.value.to));

    return matchesPrice && matchesDate;
  });
});

onMounted(() => {
  fetchUser();
});
</script>

  
<style>
.shoppings-component {
  padding: 20px;
}

.filter-comp {
  padding: 10px;
  background-color: #C46200;
}

.search-div {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
}

.search-div input {
  padding: 10px;
  font-size: 16px;
  align-items: center;
  border: 0.2px solid #964B00;
  background-color: white;
  border-radius: 10px;
  padding-right: 30px;
  font-family: 'Montserrat', sans-serif;
}

.search-div input:focus {
  border: 0.2px solid #964B00;
  outline: none;
}

.search-div i {
  position: relative;
  left: -25px;
}

.sort-div {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.ascending-div, .descending-div {
  margin-bottom: 5px;
}

.sort-div label {
  margin: 5px;
}

.filter-div1 {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.price-div, .date-div {
  display: flex;
  flex-direction: column;
}

.price-div p, .date-div p {
  margin: 5px 5px;
  font-family: 'Montserrat', sans-serif;
  font-size: 15px;
  color: #EDDEA4;
}

.price-div div input, .date-div div input {
  padding: 10px;
  font-size: 15px;
  align-items: center;
  border: 0.2px solid #964B00;
  background-color: white;
  border-radius: 10px;
  font-family: 'Montserrat', sans-serif;
}

.price-div input:focus, .date-div input:focus {
  border: 0.2px solid #964B00;
  outline: none;
}

.separator {
  display: flex;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
  padding: 0 10px;
  color: #EDDEA4;
}

.shoppings-list {
  margin-top: 20px;
}

.shopp-item {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  display: flex;
  justify-content: space-between;
}

.info-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.basic-info {
  display: block;
}

.basic-info div {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin: 5px;
}

.basic-info div p {
  font-family: 'Montserrat', sans-serif;
  margin-left: 5px;
  font-size: 15px;
}

.status-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
}

.status-info div{
  display: flex;
  font-family: 'Montserrat', sans-serif;
}

.status-info button{
  background-color: transparent;
  outline: none;
  border: none;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
  color: red;
  cursor: pointer;
}

.comment-form-div {
  width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #f9f9f9;
}

.comment-form-div h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input, .form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

.form-group input[readonly] {
  background-color: #e9e9e9;
}

.form-group textarea {
  height: 100px;
  resize: vertical;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

.name-user-div{
    display: flex;
    align-items: center;
    font-family: 'Montserrat', sans-serif;
    align-items: flex-start;
}

</style>
  
