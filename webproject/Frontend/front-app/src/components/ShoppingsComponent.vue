<template>
  <div class="shoppings-component">
    <div class="filter-comp">
      <div class="search-div">
        <input type="text" placeholder="Search..." v-model="searchName">
        <i class="fa fa-search"><img src="Icons/magglas.png" width="15px"></i>
      </div>
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
    <div class="commentDiv2" v-if="showCommentForm">
      <form @submit.prevent="submitComment">
        <input type="hidden" v-model="comment.userId" />
        <input type="hidden" v-model="comment.factoryId" />
        <div>
          <label for="comment">Comment:</label>
          <textarea id="comment" v-model="comment.text" required></textarea>
        </div>
        <div>
          <label for="rating">Rating:</label>
          <select id="rating" v-model="comment.rating" required>
            <option value="" disabled>Select rating</option>
            <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
        <button type="submit">Submit</button>
        <button @click="showCommentForm = false" type="button">Cancel</button>
      </form>
    </div>
    <div class="shoppings-list">
      <div v-for="shopp in filteredShoppings" :key="shopp.id" class="shopp-item">
        <div class="info-container">
          <div class="basic-info">
            <div>
              <p>Purchase completed:</p>
              <p>{{ formatDate(shopp.date) }}</p>
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
          <div class="status-info">
            <div>
              <p>Status: </p>
              <p> {{ shopp.status }}</p>
            </div>
            <button @click="deleteShopping(shopp.idShopping, shopp.price)" v-if="shopp.status === 'PROCESSING'">Cancel</button>
            <button @click="leaveComment(shopp.factoryId)" v-if="shopp.status === 'APPROVED' && !shopp.commented" style="color: green;">Leave Comment</button>

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
const username = sessionStorage.getItem('username');
const userId = sessionStorage.getItem('userId');
const sortKey = ref('factoryName-asc');
const priceFilter = ref({ from: null, to: null });
const dateFilter = ref({ from: null, to: null });
const showCommentForm = ref(false);

const comment = ref({
  customerId: userId,
  factoryId: null,
  text: '',
  rating: null
});

const fetchShoppings = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppings/username?username=${username}`);
    const filteredShoppings = response.data.filter(shopp => !shopp.canceled);

    const shoppingPromises = filteredShoppings.map(async (shopp) => {
      const factResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactories/${shopp.factoryId}`);
      shopp.factoryName = factResponse.data.name;
      shopp.showCommentForm = false;
      shopp.commented = false;
      return shopp;
    });

    shoppings.value = await Promise.all(shoppingPromises);
  } catch (error) {
    console.log("error fetching shoppings: ", error);
  }
};

const deleteShopping = async (id, price) => {
  try {
    await axios.delete(`http://localhost:8080/WebShopAppREST/rest/shoppings/shopp?id=${id}&username=${username}`);
    await axios.put(`http://localhost:8080/WebShopAppREST/rest/user/deductUserPoints?username=${username}&price=${price}`);
    shoppings.value = shoppings.value.filter(shopp => shopp.idShopping !== id);
  } catch (error) {
    console.error(`Error deleting shopping with id ${id}:`, error);
  }
};

const handleCommentSubmitted = () => {
  showCommentForm.value = false;
};

const leaveComment = (factoryId) => {
  comment.value.factoryId = factoryId;
  comment.value.text = '';
  comment.value.rating = null;
  showCommentForm.value = true;
};

const submitComment = async () => {
  try {
    await axios.post('http://localhost:8080/WebShopAppREST/rest/comments', comment.value);
    showCommentForm.value = false;
  } catch (error) {
    console.error("Error submitting comment: ", error);
  }
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
    const matchesSearch = shopp.factoryName && shopp.factoryName.toLowerCase().includes(searchName.value.toLowerCase());
    const matchesPrice = (!priceFilter.value.from || shopp.price >= priceFilter.value.from) &&
                         (!priceFilter.value.to || shopp.price <= priceFilter.value.to);
    const matchesDate = (!dateFilter.value.from || new Date(shopp.date) >= new Date(dateFilter.value.from)) &&
                        (!dateFilter.value.to || new Date(shopp.date) <= new Date(dateFilter.value.to));

    return matchesSearch && matchesPrice && matchesDate;
  });
});

onMounted(() => {
  fetchShoppings();
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

.commentDiv2 {
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 20px;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.commentDiv2 form {
  display: flex;
  flex-direction: column;
}

.commentDiv2 .form-group {
  margin-bottom: 15px;
}

.commentDiv2 label {
  font-family: 'Montserrat', sans-serif;
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.commentDiv2 textarea,
.commentDiv2 select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-family: 'Montserrat', sans-serif;
  background-color: #fff;
}

.commentDiv2 textarea{
  width: 95%;
}

.commentDiv2 textarea:focus,
.commentDiv2 select:focus {
  border-color: #964B00;
  outline: none;
}

.commentDiv2 .form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.commentDiv2 button {
  padding: 10px 15px;
  font-size: 14px;
  font-family: 'Montserrat', sans-serif;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.commentDiv2 button[type="submit"] {
  background-color: #4CAF50;
  margin-top: 10px;
  margin-bottom: 10px;
  color: #fff;
}

.commentDiv2 button[type="button"] {
  background-color: #f44336;
  color: #fff;
}

</style>