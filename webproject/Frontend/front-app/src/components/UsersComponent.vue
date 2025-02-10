<template>
    <div class="users-component">
        <div class="filter-comp">
            <div class="search-div">
                <input type="text" placeholder="Search..." v-model="searchName">
                <i class="fa fa-search"><img src="Icons/magglas.png" width="15px"></i>
            </div>
            <div class="sort-div">
                <div class="ascending-div">
                    <p style="margin-bottom: 5px;">Asccending</p>
                    <label>
                        <input type="radio" v-model="sortKey" value="name-asc">
                        Sort by name
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="lastname-asc">
                        Sort by lastname
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="username-asc">
                        Sort by username
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="points-asc">
                        Sort by points
                    </label>
                </div>
                <div class="descending-div">
                    <p style="margin-bottom: 5px;">Descending</p>
                    <label>
                        <input type="radio" v-model="sortKey" value="name-desc">
                        Sort by name
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="lastname-desc">
                        Sort by lastname
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="username-desc">
                        Sort by username
                    </label>
                    <label>
                        <input type="radio" v-model="sortKey" value="points-desc">
                        Sort by points
                    </label>
                </div>
            </div>
            <div class="filter-div1">
                <div class="type-div">
                    <label><input type="checkbox" value="MANAGER" v-model="selectedTypes"> Manager</label>
                    <label><input type="checkbox" value="CUSTOMER" v-model="selectedTypes"> Customer</label>
                    <label><input type="checkbox" value="WORKER" v-model="selectedTypes"> Worker</label>
                    <label><input type="checkbox" value="ADMINISTRATOR" v-model="selectedTypes"> Administrator</label>
                </div>
                <div class="separator">|</div>
                <div class="customer-type-div">
                    <label><input type="checkbox" value="GOLD" v-model="selectedCustomerTypes"> Gold</label>
                    <label><input type="checkbox" value="SILVER" v-model="selectedCustomerTypes"> Silver</label>
                    <label><input type="checkbox" value="BRONZE" v-model="selectedCustomerTypes"> Bronze</label>
                    <label><input type="checkbox" value="NOTHING" v-model="selectedCustomerTypes"> Nothing</label>
                </div>
            </div>
        </div>
        <div class="users-list">
            <div v-for="user in filteredCustomers" :key="user.id" class="user-item">
                <img src="profilePicture.png" alt="profile" width="75px" height="75px">
                <div class="user-info">
                    <p>{{ user.username }}</p>
                    <p>{{ user.name }} {{ user.lastName }}</p>
                    <p>{{ user.userType }}</p>
                    <p>{{ user.points }}</p>
                    <button @click="deleteUser(user.username)" class="deleteUser">Delete</button>
                </div>
                <div class="user-functions">
                    <p>Active: 10h</p>
                    <div class="buttons">
                      <button v-if="user.userType === 'WORKER'" @click.prevent="promoteToManager(user.id)">Promote to manager</button>
                      <button v-if="user.userType !== 'ADMINISTRATOR' && !user.blocked" @click.prevent="blockUser(user.username)">Block user</button>
                      <p>{{  user.suspicious }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
  
<script setup>
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';

const customers = ref([]);
const searchName = ref('');
const selectedTypes = ref([]);
const selectedCustomerTypes = ref([]);
const sortKey = ref('name-asc');

const fetchCustomer = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/user/')
    .then(response => {
      customers.value = response.data;
      console.log(customers.value);
    }).catch(error => {
      console.log("error: ", error);
    });
};

const deleteUser = (username) => {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/user/${username}`)
    .then(response => {
      customers.value = customers.value.filter(customer => customer.username !== username);
    })
    .catch(error => {
      console.error('Error deleting user', error);
    });
};

const sortUsers = (key, order) => {
  customers.value.sort((a, b) => {
    if (order === 'asc') {
      return a[key] > b[key] ? 1 : -1;
    } else {
      return a[key] < b[key] ? 1 : -1;
    }
  });
};

const filteredCustomers = computed(() => {
  const sortOrder = sortKey.value.split('-');
  const key = sortOrder[0];
  const order = sortOrder[1];

  let sortedUsers = [...customers.value];
  sortedUsers.sort((a, b) => {
    if (order === 'asc') {
      return a[key] > b[key] ? 1 : -1;
    } else {
      return a[key] < b[key] ? 1 : -1;
    }
  });

  return sortedUsers.filter(user => {
    const matchesSearch = (user.name && user.name.toLowerCase().includes(searchName.value.toLowerCase())) ||
                          (user.lastName && user.lastName.toLowerCase().includes(searchName.value.toLowerCase())) ||
                          (user.username && user.username.toLowerCase().includes(searchName.value.toLowerCase()));
    
    const matchesType = selectedTypes.value.length === 0 || selectedTypes.value.includes(user.userType);
    const matchesCustomerType = selectedCustomerTypes.value.length === 0 || selectedCustomerTypes.value.includes(user.customerType);
    
    return matchesSearch && matchesType && matchesCustomerType;
  });
});

const promoteToManager = (id) => {
    axios.put(`http://localhost:8080/WebShopAppREST/rest/user/promoteToManager?id=${id}`)
    .then(response => {
        customers.value = response.data;
    }).catch(error => {
        console.log("error: ", error);
    });
}

const blockUser = (username) => {
  axios.put(`http://localhost:8080/WebShopAppREST/rest/user/updateBlock?username=${username}`)
    .then(response => {
        alert("You successfuly block user");
    }).catch(error => {
      console.log("error: ", error);
    });
};

onMounted(() => {
  fetchCustomer();
});
</script>

  
<style>
.users-component {
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
  padding: 5px;
  font-size: 16px;
  align-items: center;
  border: 0.2px solid #964B00;
  background-color: white;
  border-radius: 10px;
  padding: 0.7%;
  font-family: 'Montserrat', sans-serif;
  font-size: 15px;
  border-radius: 4px;
  padding-right: 30px;
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

.ascending-div, .descending-div{
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

.type-div, .customer-type-div {
  display: flex;
  align-items: center;
}

.type-div label, .customer-type-div label {
  margin: 5px 5px;
  font-family: 'Montserrat', sans-serif;
  font-size: 15px;
  color:#EDDEA4;
}

.separator {
  display: flex;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
  padding: 0 10px;
  color:#EDDEA4;
}

.users-list {
  margin-top: 20px;
}

.user-item {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  display: flex;
}

.user-info{
    font-family: 'Montserrat', sans-serif;
    margin-left: 5px;
    margin-top: -5px;
}

.user-info p{
  font-size: 13px;
}

.user-info p:first-child {
  font-weight: bold;
  font-size: 17px;
}

.user-functions {
  margin-left: auto;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.user-functions .buttons {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
  margin-top: 5px;
}

.user-functions p{
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;

}

.user-functions button {
    background-color: white;
    border: none;
    padding: 0;
    text-align: end;
    cursor: pointer;
    color: #964B00;
    font-weight: bold;
}

.deleteUser{
  background-color: transparent;
  border: 0;
  color: #964B00;
  padding: 0;
  text-align: left;
}

.deleteUser:hover{
  background-color: transparent;
}
</style>
  