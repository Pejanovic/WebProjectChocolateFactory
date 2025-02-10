<template>
    <div class="users-component">
            <div class="users-list">
                <div v-for="user in filteredCustomers" :key="user.id" class="user-item1">
                    <img src="profilePicture.png" alt="profile" width="75px" height="75px">
                    <div class="user-info">
                        <p>{{ user.username }}</p>
                        <p>{{ user.name }} {{ user.lastName }}</p>
                        <p>{{ user.userType }}</p>
                    </div>
                    <div class="user-functions">
                        <p>Active: 10h</p>
                        <button class="promote-manager-btn" v-if="user.userType === 'CUSTOMER'" @click.prevent="promoteToManager(user.id)">Promote to manager</button>
                    </div>
                </div>
            </div>
    </div>
</template>
  
<script setup>
import axios from 'axios';
import { onMounted, ref, computed } from 'vue';

const customers = ref([]);
const searchName = ref('');
const selectedTypes = ref([]);
const selectedCustomerTypes = ref([]);
const sortKey = ref('name-asc');

const fetchCustomer = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/user/customers')
    .then(response => {
      customers.value = response.data;
      console.log(customers.value);
    }).catch(error => {
      console.log("error: ", error);
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
                          (user.lastname && user.lastname.toLowerCase().includes(searchName.value.toLowerCase())) ||
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

onMounted(() => {
  fetchCustomer();
});
</script>

  
<style>
.users-component {
  padding: 20px;
}

.users-list {
  margin-top: 20px;
  background-color: white;
}

.user-item1 {
  padding: 10px;
  background-color: white;
  border-bottom: 1px solid #ccc;
  display: flex;
}

.user-info{
    font-family: 'Montserrat', sans-serif;
    margin: 10px;
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

.user-functions p{
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;

}

.user-functions button{
    background-color: white;
    border: none;
    cursor: pointer;
    color: green;
    font-weight: bold;
}
</style>
  