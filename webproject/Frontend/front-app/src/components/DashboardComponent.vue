<template>
  <h1 class="title">Dashboard</h1>
  <div class="personal-information-container">
    <div class="personal-information">
      <div class="personal-parent-div">
        <p>Personal information</p>
        <div class="line"></div>
      </div>
      <div class="adult-info">
        <form @submit.prevent="updateUser" class="updateUserForm">
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="user.username" required />

          <label for="password">Password:</label>
          <input type="text" id="password" v-model="user.password" required />

          <label for="name">Name:</label>
          <input type="text" id="name" v-model="user.name" required />

          <label for="lastname">Last Name:</label>
          <input type="text" id="lastname" v-model="user.lastName" required />

          <label for="gender">Gender:</label>
          <select id="gender" v-model="user.gender" required>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="other">Other</option>
          </select>

          <label for="birthday">Birthday:</label>
          <input type="date" id="birthday" v-model="user.birthday" required />

          <button type="submit" class="login-button">Update</button>
        </form>
      </div>
      <div class="line"></div>
      <div class="type-div">
        <div class="user-type">{{ user.userType }}</div>
        <div class="circle" v-if="user.customerType === 'GOLD'"></div>
        <div class="circleSilver" v-if="user.customerType === 'SILVER'"></div>
        <div class="circleBronze" v-if="user.customerType === 'BRONZE'"></div>
      </div>
      <div class="customer-points">
        <p>Points:</p>
        <p>{{ user.points }}</p>
      </div>
    </div>
  </div>
  <br />
  <div class="menu-container" v-if="user.userType === 'ADMINISTRATOR'">
    <div class="menu">
      <ul>
        <li @click="event => showDiv('create-factory-div', event)" class="default-active">Create factory</li>
        <li @click="event => showDiv('users-div', event)">Users</li>
        <li @click="event => showDiv('comments-div', event)">Comments</li>
      </ul>
      <div class="create-factory-div">
        <div class="login-form1">
          <form v-if="showRegisterManagerForm" @submit.prevent="registerManager">
            <label for="name">Name:</label>
            <input type="text" id="name" v-model="newManager.name" required />

            <label for="lastname">Last Name:</label>
            <input type="text" id="lastname" v-model="newManager.lastname" required />

            <label for="username">Username:</label>
            <input type="text" id="username" v-model="newManager.username" required />

            <label for="password">Password:</label>
            <input type="password" id="password" v-model="newManager.password" required />

            <label for="gender">Gender:</label>
            <select id="gender" v-model="newManager.gender" required>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>

            <label for="birthday">Birthday:</label>
            <input type="date" id="birthday" v-model="newManager.birthday" required />

            <button type="submit" class="login-button">Register</button>
          </form>

          <p v-if="successMessage" class="successMessage">{{ successMessage }}</p>
        </div>
        <form @submit.prevent="handleSubmit">
          <label for="name">Name:</label>
          <input type="text" id="name" name="name" v-model="formData.name" required />
  
          <label for="location">Location:</label>
          <select id="location" name="location" v-model="formData.locationId" required>
            <option value="" disabled>Select a location</option>
            <option v-for="location in locations" :key="location.id" :value="location.id">{{ location.street }}, {{ location.city }} - {{ location.postalCode }}</option>
          </select>
  
          <label for="working-hours">Working Hours:</label>
          <input type="text" id="working-hours" name="workingHours" v-model="formData.workingHours" required />
  
          <label for="manager">Manager:</label>
          <select id="manager" name="manager" v-model="formData.managerId" required @click="checkManagersList">
            <option value="" disabled>Select a manager</option>
            <option v-for="manager in managers" :key="manager.id" :value="manager.id">{{ manager.name }} {{ manager.lastName }}, {{manager.username}}</option>
          </select>
          <br />
  
          <input type="file" id="image" @change="onFileChange" />
          <div v-if="imageUrl" class="image-preview">
            <p>Image Preview:</p>
            <img :src="imageUrl" width="75px" alt="Image Preview" />
          </div>
          <button type="submit">Create</button>
        </form>
      </div>
      <div class="users-div">
        <UsersComponent />
      </div>
      <div class="comments-div">
        <h2>Comments</h2>
        <DashboardComments :userRole="user.userType" />
      </div>
    </div>
  </div>
  <br>
  <div class="menu-customer" v-if="user.userType === 'CUSTOMER'">
    <div class="menu">
      <ul>
        <li @click="event => showDiv('view-products-div', event)" class="default-active">View Products</li>
        <li @click="event => showDiv('comment-div', event)">Comment</li>
        <li @click="event => showDiv('profile-div', event)">Profile</li>
      </ul>
      <div class="view-products-div">
        <ShoppingsComponent  @shoppingDeleted="onShoppingDeleted"/>
      </div>
      <div class="comment-div">
        <p>Comment</p>
      </div>
      <div class="profile-div">
        <p>Profile</p>
        <!-- Add your profile component or content here -->
      </div>
    </div>
  </div>
  <div class="menu-manager" v-if="user.userType === 'MANAGER'">
    <div class="menu">
      <ul>
        <li @click="event => showDiv('new-employer-div', event)" class="default-active">Add new employer</li>
        <li @click="event => showDiv('new-chocolate-div', event)">New chocolate</li>
        <li @click="event => showDiv('shopping-div-manager', event)">Shoppings</li>
        <li @click="event => showDiv('comments-div', event)">Comments</li>
      </ul>
      <div class="new-employer-div">
        <RegisterEmployerComponent />
      </div>
      <div class="new-chocolate-div">
        <ChocolateCRUDComponent />
      </div>
      <div class="shopping-div-manager">
        <ShoppingManagerComponent />
      </div>
      <div class="comments-div">
        <h2>Comments</h2>
        <DashboardComments :userRole="user.userType" />
      </div>
    </div>
  </div>
  <div class="worker-manager" v-if="user.userType === 'WORKER'">
    <div class="menu">
      <ul>
        <li @click="event => showDiv('update-chocolate-div', event)" class="default-active">Update chocolate</li>
      </ul>
      <div class="update-chocolate-div">
        <UpdateChocolateWorker />
      </div>
    </div>
  </div>
  <br>
</template>
  
<script setup>
import axios from 'axios';
import { nextTick, onMounted, ref } from 'vue';
import ChocolateCRUDComponent from './ChocolateCRUDComponent.vue';
import DashboardComments from './DashboardComments.vue';
import RegisterEmployerComponent from './RegisterEmployerComponent.vue';
import ShoppingManagerComponent from './ShoppingManagerComponent.vue';
import ShoppingsComponent from './ShoppingsComponent.vue';
import UpdateChocolateWorker from './UpdateChocolateWorker.vue';
import UsersComponent from './UsersComponent.vue';

const managers = ref([]);
const username = sessionStorage.getItem('username');
const imageUrl = ref('');
const selectedFile = ref(null);
const user = ref({});
const locations = ref([]);
const emit = defineEmits(['add-factory']);
const successMessage = ref("");

const newManager = ref({
  username: '',
  password: '',
  name: '',
  lastname: '',
  birthday: '',
  gender: '',
});

const selectedUser = ref(null);
const showRegisterManagerForm = ref(false);
const checkManagersList = () => {
  if (managers.value.length === 0) {
    showRegisterManagerForm.value = true;
  } else {
    showRegisterManagerForm.value = false;
  }
};

const formData = ref({
  name: '',
  locationId: '',
  workingHours: '',
  managerId: '',
  logo: ''
});

const onShoppingDeleted = () => {
  fetchUser(username);
};

const registerManager = () => {
  const formatter = new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });

  const [month, day, year] = formatter.format(new Date(newManager.value.birthday)).split('/');
  const formattedDate = `${month}/${day}/${year}`;

  axios
    .post(`http://localhost:8080/WebShopAppREST/rest/user/register/manager?username=${newManager.value.username}&password=${newManager.value.password}&name=${newManager.value.name}&lastname=${newManager.value.lastname}&birthday=${formattedDate}&gender=${newManager.value.gender}`, {})
    .then((response) => {
      successMessage.value = 'Successfully registered';
      console.log(response.data.id);
      formData.value.managerId = response.data.id;
      getManagers();
      setTimeout(() => {
        
      }, 500);
    })
    .catch((error) => {
      console.error(error);
      successMessage.value = 'Error registering manager: ' + error.response.data;
    });
};

const fetchUser = async (username) => {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
    user.value = response.data;
    sessionStorage.setItem('userId', user.value.id);
  } catch (error) {
    console.error("Došlo je do greške prilikom preuzimanja korisnika", error);
  }
};

const updateUser = async () => {
  try {
    const formatter = new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });

    const [month, day, year] = formatter.format(new Date(user.value.birthday)).split('/');
    const formattedDate = `${month}/${day}/${year}`;

    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/user/updateUser?id=${user.value.id}&username=${user.value.username}&password=${user.value.password}&name=${user.value.name}&lastname=${user.value.lastName}&birthday=${formattedDate}&gender=${user.value.gender}`, {}, {
      withCredentials: true
    });
    console.log('Korisnik je uspešno ažuriran', response.data);
    user.value = response.data;
    alert('User updated successfully');
  } catch (error) {
    console.error("Došlo je do greške prilikom ažuriranja korisnika", error);
    alert('Error updating user');
  }
};

const handleSubmit = async () => {
  if (!formData.value.name || !formData.value.locationId || !formData.value.workingHours || !formData.value.managerId || !selectedFile.value) {
    alert("All fields are required");
    return;
  }

  try {
    const imagePayload = {
      logo: formData.value.logo,
      name: formData.value.name
    };

    const imageResponse = await axios.post('http://localhost:8080/WebShopAppREST/rest/imageupload', imagePayload, {
      withCredentials: true
    });
    formData.value.logo = imageResponse.data;

    const factoryResponse = await axios.post('http://localhost:8080/WebShopAppREST/rest/chocolateFactories', formData.value, {
      withCredentials: true
    });
    console.log('Successfully added factory with image path', factoryResponse.data);
    emit('update-shopping', factoryResponse.data);
  } catch (error) {
    if (error.response && error.response.config.url.includes('imageupload')) {
      console.error('Error uploading image:', error);
    } else if (error.response && error.response.config.url.includes('chocolateFactories')) {
      console.error('Error adding factory:', error);
    } else {
      console.error('An unexpected error occurred:', error);
    }
  }
};

const getLocation = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/locations')
        .then(response => {
          locations.value = response.data;
        }).catch( error => {
          console.error("Error fetching locations:", error);
        });
};

const getManagers = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/user/free/managers', { withCredentials: true })
    .then(response => {
      managers.value = response.data;
    })
    .catch(error => {
      console.error("Error fetching managers:", error);
    });
};

const showDiv = (className, event) => {
  if (!className) return;

  const divs = document.querySelectorAll('.create-factory-div, .users-div, .comments-div, .view-products-div, .order-history-div, .profile-div, .new-employer-div, .new-chocolate-div, .shopping-div-manager, .update-chocolate-div, .comment-div');
  divs.forEach((div) => {
    div.style.display = 'none';
  });

  const selectedDiv = document.querySelector('.' + className);
  if (selectedDiv) {
    selectedDiv.style.display = 'block';
  }

  const allItems = document.querySelectorAll('.menu ul li');
  allItems.forEach(item => {
    item.classList.remove('active');
  });

  if (event && event.currentTarget) {
    const selectedElement = event.currentTarget;
    selectedElement.classList.add('active');
  } else {
    const defaultActiveElement = document.querySelector('.default-active');
    if (defaultActiveElement) {
      defaultActiveElement.classList.add('active');
    }
  }
};

const onFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
      formData.value.logo = e.target.result.split(',')[1];
    };
    reader.readAsDataURL(file);
    selectedFile.value = file;
  }
};

onMounted(async () => {
  await fetchUser(username);
  nextTick(() => {
    if (user.value.userType === 'ADMINISTRATOR') {
      showDiv('create-factory-div');
    } else if (user.value.userType === 'CUSTOMER') {
      showDiv('view-products-div');
    } else if(user.value.userType === 'MANAGER'){
      showDiv('new-employer-div');
    }
    getManagers();
    getLocation();
    console.log(user.value);
  });
});
</script>

<style>
.title {
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    margin-top: 10px;
    text-align: center;
}

.personal-information-container {
    display: flex;
    justify-content: center;
    margin: 20px;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
}

.personal-information {
    width: 80%;
    border: 1px solid black;
    border-radius: 20px;
    padding: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.personal-parent-div {
    display: block;
}

p {
    margin: 0;
}

.line {
    width: 100%;
    height: 0.2px;
    background-color: black;
    margin-top: 10px;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.gender-div {
    display: flex;
    justify-content: center;
    width: 100%;
    padding: 10px;
}

.male, .women {
    border: 1px solid black;
    border-radius: 10px;
    padding: 10px;
    display: flex;
    margin: 10px;
    align-items: center;
    width: 45%;
}

.adult-info {
    margin-top: 5px;
}

.adult-info form {
    display: flex;
    flex-direction: column;
}

.adult-info label {
    margin-top: 10px;
}

.adult-info input {
    margin-bottom: 10px;
    border: 0.5px solid black;
    border-radius: 10px;
    padding: 10px;
    font-family: 'Montserrat', sans-serif;
}

.type-div {
    margin: 10px;
    display: flex;
    align-items: center;
}

.user-type {
    margin-right: 5px;
}

.circle {
    height: 10px;
    width: 10px;
    background-color: gold;
    border-radius: 50%;
}
.circleSilver {
    height: 10px;
    width: 10px;
    background-color: silver;
    border-radius: 50%;
}
.circleBronze{
    height: 10px;
    width: 10px;
    background-color: #cd7f32;
    border-radius: 50%;
}

.customer-points {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-top: auto;
}

.customer-points p {
    font-size: 12px;
    margin-left: 10px;
}

.menu-container {
    display: flex;
    justify-content: center;
    width: 100%;
}

.menu-customer {
    display: flex;
    justify-content: center;
    width: 100%;
}

.menu-manager {
    display: flex;
    justify-content: center;
    width: 100%;
}

.worker-manager{
    display: flex;
    justify-content: center;
    width: 100%;
}

.menu {
    width: 80%;
    border: 1px solid black;
    border-radius: 10px;
    padding: 10px;
}

.menu ul {
    display: flex;
    justify-content: flex-start;
    padding: 0;
    margin: 0;
}

.menu ul li {
    list-style: none;
    padding: 10px 25px;
    background-color: #C46200;
    color: white;
    cursor: pointer;
    transition: background-color 0.3s;
}

.menu ul li:first-child {
    margin-left: 10px;
}

.active {
    background-color: #964B00 !important;
}

.menu ul li:hover {
    background-color: #964B00;
}

.create-factory-div, .users-div, .comments-div {
    width: calc(100% - 40px);
    margin-left: 10px;
    padding: 10px;
    border: 1px solid black;
    display: none;
}

.create-factory-div form {
    display: flex;
    flex-direction: column;
}

.create-factory-div label {
    margin-top: 10px;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
}

.create-factory-div input,
.create-factory-div select,
.create-factory-div button {
    margin-bottom: 10px;
    border: 0.5px solid black;
    border-radius: 10px;
    padding: 10px;
    font-family: 'Montserrat', sans-serif;
}
.create-factory-div button{
    background-color: #EDDEA4;
    border: none;
    color: white;
    cursor: pointer;
}
.image-preview {
  margin-top: 10px;
}

.image-preview img {
  max-width: 100%;
  height: auto;
}

.customer-to-manager {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 1px solid black;
  background-color: white;
  border-radius: 30px;
  height: 80%;
  width: 80%;
  overflow: hidden;
  transform: translate(-50%, -20%);
}

.updateUserForm select{
  font-family: 'Montserrat', sans-serif;
  padding: 10px;
}

.updateUserForm button:hover{
  background-color: #964B00;
}
</style>