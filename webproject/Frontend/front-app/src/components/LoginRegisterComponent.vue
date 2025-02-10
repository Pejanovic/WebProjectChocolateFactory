<template>
    <br />
    <div class="main-div">
      <div class="login-div">
        <div class="text-login">
          <p class="hello">Hello,</p>
          <p class="welcome">Welcome</p>
        </div>
        <div class="login-form" id="login-form-login">
          <form @submit.prevent="loginUser">
            <label for="username">Username:</label>
            <input type="text" id="username" v-model="loginData.username" required />
  
            <label for="password">Password:</label>
            <input type="password" id="password" v-model="loginData.password" required />
  
            <div class="keep-signed-in">
              <input type="checkbox" id="keep-signed-in" name="keep-signed-in" />
              <label for="keep-signed-in">Keep me signed in</label>
            </div>
            <button type="submit" class="login-button">Login</button>
          </form>
          <div class="to-register">
            <button class="register-button" id="goto-register">I don't have an account</button>
            <button class="register-button" id="goto-register" @click="continueAsGuest">Continue as guest</button>
          </div>
        </div>
      </div>
      <div class="register-div">
        <div class="text-login">
          <p class="welcome">Nice to see you,</p>
          <p class="hello">Welcome back,</p>
        </div>
        <div class="login-form" id="login-form-register">
          <form @submit.prevent="registerUser">
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
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="other">Other</option>
            </select>
  
            <label for="birthday">Birthday:</label>
            <input type="date" id="birthday" v-model="user.birthday" required />
  
            <button type="submit" class="login-button">Register</button>
          </form>
          <div class="to-register">
            <button class="register-button" id="goto-login">I have an account</button>
            <p class="successMessage">{{ success }}</p>
          </div>
        </div>
      </div>
    </div>
    <br />
  </template>
<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const success = ref('');
const router = useRouter();

const user = ref({
  username: '',
  password: '',
  name: '',
  lastname: '',
  gender: 'Male',
  birthday: ''
});


const loginData = ref({
  username: '',
  password: ''
});

function continueAsGuest(){
  axios.post(`http://localhost:8080/WebShopAppREST/rest/user/login?username=guest&password=guest`, {}, {
      withCredentials: true 
  })
  .then((response) => {
      console.log('Login successful');
      const data = response.data;
      const user = data.user;
      const sessionId = data.sessionId;

      sessionStorage.setItem('username', user.username);
      sessionStorage.setItem('userType', user.userType);
      sessionStorage.setItem('userId', user.id);
      sessionStorage.setItem('sessionId', sessionId);

      router.push({ name: 'home', query: { username: user.username, type: user.userType } });
  })
  .catch((error) => {
    alert(`Error: ${error.response.status} - ${error.response.data}`);
  });
}

function registerUser() {
  const formatter = new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });

  const [month, day, year] = formatter.format(new Date(user.value.birthday)).split('/');
  const formattedDate = `${month}/${day}/${year}`;

  axios
    .post(`http://localhost:8080/WebShopAppREST/rest/user/register?username=${user.value.username}&password=${user.value.password}&name=${user.value.name}&lastname=${user.value.lastname}&birthday=${formattedDate}&gender=${user.value.gender}`, {})
    .then((response) => {
      success.value = 'Uspjesno ste se registrovali';
      setTimeout(() => {
        document.getElementById('login-form-login').classList.remove('hidden');
        document.getElementById('login-form-login').style.display = 'flex';
        document.getElementById('login-form-register').classList.add('hidden');
        document.getElementById('login-form-register').style.display = 'none';
      }, 500);
    })
    .catch((error) => {
      alert(`Error: ${error.response.status} - ${error.response.data}`);
    });
}


function loginUser() {
  axios.post(`http://localhost:8080/WebShopAppREST/rest/user/login?username=${loginData.value.username}&password=${loginData.value.password}`, {}, {
      withCredentials: true 
  })
  .then((response) => {
      console.log('Login successful');
      const data = response.data;
      const user = data.user;
      const sessionId = data.sessionId;

      sessionStorage.setItem('username', user.username);
      sessionStorage.setItem('userType', user.userType);

      sessionStorage.setItem('sessionId', sessionId);

      router.push({ name: 'home', query: { username: user.username, type: user.userType } });
  })
  .catch((error) => {
    alert(`Error: ${error.response.status} - ${error.response.data}`);
  });
}

document.addEventListener('DOMContentLoaded', function () {
  const loginForm = document.getElementById('login-form-login');
  const registerForm = document.getElementById('login-form-register');

  document.getElementById('goto-register').addEventListener('click', function () {
    console.log('Switching to register form');
    loginForm.classList.add('hidden');
    setTimeout(() => {
      loginForm.style.display = 'none';
      registerForm.style.display = 'flex';
      setTimeout(() => {
        registerForm.classList.remove('hidden');
        console.log('Register form displayed');
      }, 1);
    }, 100);
  });

  document.getElementById('goto-login').addEventListener('click', function () {
    console.log('Switching to login form');
    registerForm.classList.add('hidden');
    setTimeout(() => {
      registerForm.style.display = 'none';
      loginForm.style.display = 'flex';
      setTimeout(() => {
        loginForm.classList.remove('hidden');
        console.log('Login form displayed');
      }, 1);
    }, 100);
  });
});
</script>
<style>
.main-div {
  display: flex;
  height: 700px;
  padding: 20px;
  width: 80%;
  margin: 0 auto;
}
.login-div {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('/public/background.png');
  background-size: cover;
  background-position: center;
  flex: 1;
  padding: 10px;
}
.register-div {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-image: url('/public/register-back.png');
  background-size: cover;
  background-position: center;
  flex: 1;
  padding: 10px;
}
.text-login {
  text-align: center;
}
.text-login p {
  color: white;
  font-family: 'Montserrat', sans-serif;
  margin: 0;
}
.text-login .hello {
  font-size: 75px;
}
.text-login .welcome {
  font-size: 25px;
  text-align: left;
  margin-left: 5%;
}
.login-form {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  height: 90%;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 1;
  transition: opacity 0.5s;
}
.hidden {
  opacity: 0;
  pointer-events: none;
}
.login-form form {
  display: flex;
  flex-direction: column;
  width: 60%;
}
.login-form label {
  color: black;
  margin-bottom: 5px;
  font-family: 'Montserrat', sans-serif;
}
.login-form input[type='text'],
.login-form input[type='password'],
.login-form input[type='date'],
.login-form select {
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
  font-size: 16px;
  font-family: 'Montserrat', sans-serif;
  outline: none;
}
.keep-signed-in {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.keep-signed-in label {
  color: black;
  margin-left: 5px;
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
.to-register {
  position: absolute;
  bottom: 20px;
  left: 20px;
}
.register-button {
  background: none;
  border: none;
  color: #007bff;
  font-family: 'Montserrat', sans-serif;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
}
.register-button:hover {
  color: #0056b3;
}
#login-form-register {
  display: none;
}
.successMessage {
  color: green;
  margin-top: 10px;
}
</style>
  