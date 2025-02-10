<template>
    <div class="login-form1">
      <form @submit.prevent="registerUser1">
        <label for="name">Name:</label>
        <input type="text" id="name" v-model="user.name" required />
  
        <label for="lastname">Last Name:</label>
        <input type="text" id="lastname" v-model="user.lastname" required />
  
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="user.username" required />
  
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="user.password" required />
  
        <label for="gender">Gender:</label>
        <select id="gender" v-model="user.gender" required>
          <option value="male">Male</option>
          <option value="female">Female</option>
        </select>
  
        <label for="birthday">Birthday:</label>
        <input type="date" id="birthday" v-model="user.birthday" required />
  
        <button type="submit" class="login-button">Register</button>
      </form>
  
      <p v-if="successMessage" class="successMessage">{{ successMessage }}</p>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios';
  import { ref } from 'vue';
  
  const user = ref({
    username: '',
    password: '',
    name: '',
    lastname: '',
    gender: 'male',
    birthday: ''
  });
  
  const successMessage = ref('');
  
  function registerUser1() {
    const formatter = new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  
    const [month, day, year] = formatter.format(new Date(user.value.birthday)).split('/');
    const formattedDate = `${month}/${day}/${year}`;
  
    axios
      .post(`http://localhost:8080/WebShopAppREST/rest/user/register/employer?username=${user.value.username}&password=${user.value.password}&name=${user.value.name}&lastname=${user.value.lastname}&birthday=${formattedDate}`, {})
      .then((response) => {
        successMessage.value = 'Successfully registered';
        setTimeout(() => {
          console.log("You did it");
        }, 500);
      })
      .catch((error) => {
        console.error(error);
      });
  }
  </script>
  
  <style>
    .login-form1 {
    width: 90%;
    height: 90%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 10px;
    }
  
  .login-form1 form {
    display: flex;
    flex-direction: column;
    width: 60%;
  }
  
  .login-form1 label {
    color: black;
    margin-bottom: 5px;
    font-family: 'Montserrat', sans-serif;
  }
  
  .login-form1 input[type='text'],
  .login-form1 input[type='password'],
  .login-form1 input[type='date'],
  .login-form1 select {
    padding: 10px;
    margin-bottom: 10px;
    border-radius: 4px;
    font-size: 16px;
    font-family: 'Montserrat', sans-serif;
    outline: none;
  }
  
  .login-button {
    margin-right: auto;
    height: 40px;
    width: 100%;
    font-family: 'Montserrat', sans-serif;
    border-radius: 10px;
    border: 0;
    background-color: #964b00;
    color: white;
  }
  
  .login-button:hover {
    cursor: pointer;
  }
  
  .successMessage {
    color: green;
    margin-top: 10px;
  }
  </style>
  