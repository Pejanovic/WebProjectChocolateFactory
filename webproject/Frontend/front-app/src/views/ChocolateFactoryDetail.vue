<template>
  <div v-if="factory" class="factory">
    <div class="title-container">
      <h1 class="title">{{ factory.name }}</h1>
    </div>
    <div class="parent-container">
      <div class="info-row rating">
        <img src="Icons/star (1).png" class="factory-icon" width="25px">
        <p>{{ factory.rating }}</p>
      </div>
      <div class="info-row logo">
        <img src="Icons/logo.png" class="factory-icon" width="50px">
      </div>
      <div class="info-row status">
        <p><strong>Status:</strong> {{ factory.status }}</p>
      </div>
      <div class="info-row working-hours">
        <p><strong>Working Hours:</strong> {{ factory.workingHours }}</p>
      </div>
    </div>
  </div>

  <div ref="mapContainer" class="map-container"></div>
  
  <div class="comments-section">
    <CommentList :factoryId="Number(props.id)" />
  </div>
  
  <div>
    <div class="error-div">
      <p class="choco-error">{{error}}</p>
    </div>
    <div class="chocolate-list">
      <div v-for="choco in chocolates" :key="choco.id" class="chocolate-item" @click="addChocolate(choco)">
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
          <button @click="decrementAmount(choco.id)" class="amount-button-left">-</button>
          <input type="text" :value="choco.amount || 1" class="amount-input" readonly>
          <button @click="incrementAmount(choco.id)" class="amount-button-right">+</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import CommentList from '@/components/CommentList.vue';
import axios from 'axios';
import { Map, View } from 'ol';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import 'ol/ol.css';
import { fromLonLat } from 'ol/proj';
import OSM from 'ol/source/OSM';
import VectorSource from 'ol/source/Vector';
import { Icon, Style } from 'ol/style';
import { defineEmits, defineProps, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['update-shopping']);
const factory = ref(null);
const chocolates = ref([]);
const route = useRoute();
const username = sessionStorage.getItem('username');
const mapContainer = ref(null);
const type = sessionStorage.getItem('userType');

const chocolate = ref({
  name: '',
  price: 0,
  factoryId: props.id,
  grammage: 0,
  description: '',
  category: 'DARK',
  type: 'REGULAR',
  status: 'OUT_OF_STOCK',
  quantity: 0,
});
const mode = ref("ADD");
const showAddForm = ref(false);
const showUpdateForm = ref(false);
const error = ref("");
const success = ref("");

function fetchFactoryDetail() {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactories/${props.id}`)
    .then(response => {
      factory.value = response.data;
      return getLocation(factory.value.locationId);
    })
    .then(location => {
      factory.value.location = location;
      initializeMap();
      console.log(factory.value);
    })
    .catch(error => {
      console.error('There was an error fetching the factory details!', error);
    });
}

const getLocation = (id) => {
  return axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/${id}`)
    .then(response => response.data)
    .catch(error => {
      console.error("Error fetching locations:", error);
      throw error;
    });
};

function fetchChocolates() {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/FactorySearch?id=${props.id}`)
    .then(response => {
      if (type === 'CUSTOMER') {
        chocolates.value = response.data
          .filter(choco => choco.quantity > 0)
          .map(choco => ({ ...choco, amount: 0 }));
      } else {
        chocolates.value = response.data.map(choco => ({ ...choco, amount: 0 }));
      }
    })
    .catch(error => {
      console.error('There was an error fetching the chocolates!', error);
    });
}

function addChocolate(choco) {
  axios.post(`http://localhost:8080/WebShopAppREST/rest/cart/items?chocolateId=${choco.id}&amount=${choco.amount}`, {}, {
      withCredentials: true
  })
  .then(response => {
    console.log('Chocolate added to cart successfully', response);
    emit('update-shopping');
    error.value = ""; 
  })
  .catch(err => {
    if (err.response && err.response.data) {
      error.value = err.response.data; 
    } else {
      error.value = "There was an error adding the chocolate to the cart.";
    }
    console.error('There was an error adding the chocolate to the cart', err);
  });
}

function initializeMap() {
  const map = new Map({
    target: mapContainer.value,
    layers: [
      new TileLayer({
        source: new OSM(),
      }),
    ],
    view: new View({
      center: fromLonLat([factory.value.location.longitude, factory.value.location.latitude]),
      zoom: 15,
    }),
  });

  const marker = new Feature({
    geometry: new Point(fromLonLat([factory.value.location.longitude, factory.value.location.latitude])),
  });

  marker.setStyle(
    new Style({
      image: new Icon({
        anchor: [0.5, 1],
        src: 'https://openlayers.org/en/latest/examples/data/icon.png',
      }),
    })
  );

  const vectorSource = new VectorSource({
    features: [marker],
  });

  const markerVectorLayer = new VectorLayer({
    source: vectorSource,
  });

  map.addLayer(markerVectorLayer);
}

onMounted(() => {
  console.log('Factory ID:', props.id);
  fetchFactoryDetail();
  fetchChocolates();
});

function incrementAmount(id) {
  const chocolate = chocolates.value.find(choco => choco.id === id);
  if (chocolate) {
    if (chocolate.amount < chocolate.quantity) {
      chocolate.amount += 1;
    } else {
      console.log('Količina proizvoda je dostigla maksimalnu dostupnu količinu.');
    }
  }
}

function decrementAmount(id) {
  const chocolate = chocolates.value.find(choco => choco.id === id);
  if (chocolate && chocolate.amount > 1) {
    chocolate.amount -= 1;
  }
}

function toggleAddForm() {
  showAddForm.value = !showAddForm.value;
  if (showAddForm.value) {
    showUpdateForm.value = false;
    resetForm();
    mode.value = "ADD";
  }
}

function handleSubmit() {
  if (!chocolate.value.name || !chocolate.value.price || 
      !chocolate.value.grammage || !chocolate.value.description || 
      !chocolate.value.category || !chocolate.value.type) {
    error.value = "Unesite sve elemente.";
    success.value = "";
    return;
  }
  axios.post("http://localhost:8080/WebShopAppREST/rest/chocolates/", chocolate.value)
    .then(response => {
      chocolates.value.push(response.data);
      success.value = "Chocolate successfully added!";
      error.value = "";
      resetForm();
      showAddForm.value = false;
    })
    .catch(err => {
      error.value = "There was an error adding the chocolate.";
      success.value = "";
    });
}

function handleUpdate() {
  axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolate.value.id}`, chocolate.value)
    .then(response => {
      const index = chocolates.value.findIndex(choco => choco.id === chocolate.value.id);
      chocolates.value[index] = response.data;
      success.value = "Chocolate successfully updated!";
      error.value = "";
      mode.value = "ADD";
      resetForm();
      showUpdateForm.value = false;
    })
    .catch(err => {
      error.value = "There was an error updating the chocolate.";
      success.value = "";
    });
}

function editChocolate(choco) {
  mode.value = "UPDATE";
  chocolate.value = { ...choco };
  showAddForm.value = false;
  showUpdateForm.value = true;
}

function deleteChocolate(id) {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`)
    .then(() => {
      chocolates.value = chocolates.value.filter(choco => choco.id !== id);
    })
    .catch(err => {
      error.value = "There was an error deleting the chocolate.";
    });
}

function resetForm() {
  chocolate.value = {
    name: '',
    price: 0,
    factoryId: props.id,
    grammage: 0,
    description: '',
    category: 'DARK',
    type: 'REGULAR',
    status: 'OUT_OF_STOCK',
    quantity: 0,
  };
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

.factory {
  width: 100%;
  height: 100vh;
  background-color: rgb(150, 75, 0);
  background-image: url(https://static-cse.canva.com/_next/static/assets/wave_w2881xh801_98aaf3a37b4d733bdb3bc7754c25ad3608c520c5ebb9b1079f2d0c2f78b07ab8.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  font-family: 'Montserrat', sans-serif;
  position: relative;
  color: #EDDEA4;
}

.title-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.title {
  text-align: center;
}

.parent-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.info-row {
  display: flex;
  align-items: center;
  position: absolute;
  margin: 10px;
}

.rating {
  top: 10px;
  right: 10px;
  font-weight: bold;
}

.logo {
  top: 10px;
  left: 10px;
}

.status {
  bottom: 10px;
  right: 10px;
}

.working-hours {
  bottom: 10px;
  left: 10px;
}

.factory-icon {
  margin-right: 10px;
}

.error-div{
  text-align: center;
}
.choco-error{
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
  color: red;
}

.title-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.title {
  text-align: center;
}

.info-starlocation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px;
}

.parent-container {
  position: relative;
  height: 100vh;
}

.info-row {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  align-items: center;
  margin: 10px;
}

.factory-icon {
  margin-right: 10px;
}

.head {
  justify-content: center;
  display: flex;
  align-items: center; 
}

.form-open {
  border-radius: 50%;
  width: 25px;
  height: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  margin-left: 10px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); 
}

.form-open:hover {
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
  cursor: pointer; 
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

amount-button-right {
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
}

.comments-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.comments-section h2 {
  font-family: 'Montserrat', sans-serif;
  color: #333;
  margin-bottom: 15px;
}
.map-container {
  width: 100%;
  height: 400px;
}
</style>
