<template>
  <div class="container">
    <div class="titles">
      <p class="p1"> Discover and indulge in the finest chocolate creations from top-rated chocolatiers. </p>
      <p class="p2"> Treat yourself to a world of exquisite flavors and premium quality chocolate products. </p>
    </div>
    <div class="video">
      <div class="overlay"></div>
      <video autoplay muted loop playsinline>
        <source src="chocolatevideo.mp4" type="video/mp4">
        Your browser does not support the video tag.
      </video>
    </div>
  </div>
  <div class="factories">
    <router-view @add-factory="fetchFactories"/>
    <div class="fact-header">
      <button @click.prevent="openFilter">Filter</button>
      <div class="input-container">
        <i class="fa fa-search"><img src="Icons/magglas.png" width="15px"></i>
        <input type="text" placeholder="Search..." v-model="searchName">
      </div>
    </div>
    <div class="filter-div" id="filter-div">
      <div class="sort-header">
        <p class="sort-p">Sort</p>
      </div>
      <div class="sort-div">
        <div class="sort-option">
          <label>Sort by name of factory:</label>
          <input type="radio" name="name-sort" value="name-ascend" v-model="sortOption" @change="sortFactories"> Ascend
          <input type="radio" name="name-sort" value="name-descend" v-model="sortOption" @change="sortFactories"> Descend
        </div>
        <div class="sort-option">
          <label>Sort by location:</label>
          <input type="radio" name="location-sort" value="location-ascend" v-model="sortOption" @change="sortFactories"> Ascend
          <input type="radio" name="location-sort" value="location-descend" v-model="sortOption" @change="sortFactories"> Descend
        </div>
        <div class="sort-option">
          <label>Sort by average grade:</label>
          <input type="radio" name="grade-sort" value="rating-ascend" v-model="sortOption" @change="sortFactories"> Ascend
          <input type="radio" name="grade-sort" value="rating-descend" v-model="sortOption" @change="sortFactories"> Descend
        </div>
      </div>
      <div class="filter-header">
        <p class="filter-p">Filter</p>
      </div>
      <div class="filter-work-div">
        <div class="search-container">
          <input type="text" placeholder="Search chocolates" v-model="searchChocolates">
          <input type="text" placeholder="Search locations" v-model="searchLocation">
        </div>
        <div class="rating-container">
          <label>Enter the minimum average rating:</label>
          <input type="text" v-model="searchRating">
        </div>
        <div class="chocolate-kinds">
          <label>Kind of chocolates:</label>
          <input type="checkbox" value="REGULAR" v-model="selectedChocolateKinds"> Regular
          <input type="checkbox" value="FOR_COOKING" v-model="selectedChocolateKinds"> For cooking
          <input type="checkbox" value="FOR_DRINKING" v-model="selectedChocolateKinds"> For drinking
        </div>
        <div class="chocolate-types">
          <label>Type of chocolates:</label>
          <input type="checkbox" value="Dark" v-model="selectedChocolateTypes"> Dark
          <input type="checkbox" value="Milk" v-model="selectedChocolateTypes"> Milk
          <input type="checkbox" value="White" v-model="selectedChocolateTypes"> White
        </div>
        <div class="show-opened">
          <input type="checkbox" value="justOpened" v-model="showOpenedOnly"> Show just opened factories
        </div>
      </div>
    </div>
    <div class="cards">
      <div class="card" @click="goToDetail(fact.id)" v-for="fact in filteredFactories" :key="fact.id">
        <div class="card-header">
          <img :src="`${fact.logo}`" width="25px" height="25px">
          <h3>{{ fact.name }}</h3>
          <button class="deleteFactory" @click.stop.prevent="deleteFactory(fact.id)" v-if="type === 'ADMINISTRATOR'">X</button>
        </div>
        <div class="card-location">
          <img src="Icons/location.png" width="25px" height="25px" class="icon-white">
          <p>{{ fact.location }}</p>
        </div>
        <div class="footer">
          <div class="rate">
            <img src="Icons/star (1).png" width="25px" height="25px" class="icon-white">
            <p>{{ fact.rating }}</p>
          </div>
          <p class="status" :class="{'statusW': fact.status === 'OPEN', 'statusN': fact.status !== 'OPEN'}">{{ fact.status }}</p>
        </div>
      </div>
    </div>
  </div>
  <br>
</template>

<script setup>
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const factories = ref([]);
const router = useRouter();
const sortOption = ref('name-ascend');
const searchName = ref('');
const username = sessionStorage.getItem('username');
const type = sessionStorage.getItem('userType');
const session = sessionStorage.getItem('sessionId');
const searchLocation = ref('');
const searchRating = ref('');
const searchChocolates = ref('');
const selectedChocolateTypes = ref([]);
const selectedChocolateKinds = ref([]);
const showOpenedOnly = ref(false);


const fetchFactories = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/chocolateFactories/')
    .then(response => {
      const factoryData = response.data;
      console.log(factoryData);
      let factoriesWithChocolates = [];

      factoryData.forEach(factory => {
        axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/FactorySearch?id=${factory.id}`)
          .then(chocolatesResponse => {
            factory.chocolates = chocolatesResponse.data;
            if (factory.logo) {
              const fullPath = factory.logo;
              const fileName = fullPath.split('\\').pop();
              factory.logo = fileName;
            }

            getLocation(factory.locationId).then(location => {
              factory.rating = parseFloat(factory.rating) || 0;
              factory.location = `${location.street}, ${location.city}`;
              factoriesWithChocolates.push(factory);

              if (factoriesWithChocolates.length === factoryData.length) {
                factories.value = factoriesWithChocolates;
                console.log(factories.value);
              }
            }).catch(error => {
              console.error('Error fetching location!', error);
            });
          })
          .catch(error => {
            console.error('Error fetching chocolates!', error);
          });
      });
    })
    .catch(error => {
      console.error('Error fetching factories!', error);
    });
};

const deleteFactory = (id) => {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolateFactories/${id}`)
      .then(response => {
        factories.value = factories.value.filter(fact => fact.id !== id);
      })
      .catch(error => {
        console.log(error);
      });
};


const getLocation = (id) => {
  return axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/${id}`)
    .then(response => response.data)
    .catch(error => {
      console.error("Error fetching locations:", error);
      throw error;
    });
};

const sortedFactories = computed(() => {
  let sorted = [...factories.value];
  if (sortOption.value === 'name-ascend') {
    sorted.sort((a, b) => a.name.localeCompare(b.name));
  } else if (sortOption.value === 'name-descend') {
    sorted.sort((a, b) => b.name.localeCompare(a.name));
  } else if (sortOption.value === 'location-ascend') {
    sorted.sort((a, b) => a.location.localeCompare(b.location));
  } else if (sortOption.value === 'location-descend') {
    sorted.sort((a, b) => b.location.localeCompare(a.location));
  } else if (sortOption.value === 'rating-ascend') {
    sorted.sort((a, b) => a.rating - b.rating);
  } else if (sortOption.value === 'rating-descend') {
    sorted.sort((a, b) => b.rating - a.rating);
  }
  return sorted;
});

const filteredFactories = computed(() => {
      return sortedFactories.value.filter(fact => {
        const matchesName = fact.name.toLowerCase().includes(searchName.value.toLowerCase());
        const matchesLocation = fact.location.toLowerCase().includes(searchLocation.value.toLowerCase());
        const matchesRating = !searchRating.value || fact.rating >= parseFloat(searchRating.value);
        const matchesChocolateName = fact.chocolates.some(chocolate => 
          chocolate.name.toLowerCase().includes(searchChocolates.value.toLowerCase())
        );
        const matchesChocolateType = selectedChocolateTypes.value.length === 0 || fact.chocolates.some(chocolate =>
          selectedChocolateTypes.value.map(type => type.toUpperCase()).includes(chocolate.category.toUpperCase())
        );
        const matchesChocolateKinds = selectedChocolateKinds.value.length === 0 || fact.chocolates.some(chocolate => 
          selectedChocolateKinds.value.map(kind => kind.toUpperCase()).includes(chocolate.type.toUpperCase())
        );
        const matchesStatus = !showOpenedOnly.value || fact.status === 'OPEN';

        return matchesName && matchesLocation && matchesRating && matchesChocolateName && matchesChocolateType && matchesChocolateKinds && matchesStatus;
      });
    });

const openFilter = () => {
  const filterDiv = document.getElementById('filter-div');
  filterDiv.style.display = filterDiv.style.display === 'none' ? 'block' : 'none';
};

const onFactoryAdded = (newFactory) => {
  factories.value.push(newFactory);
};

const goToDetail = (id) => {
  router.push({ name: 'ChocolateFactoryDetail', params: { id, username, type }});
};
onMounted(() => {
  fetchFactories();
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;700&family=Roboto:wght@400;700&display=swap');

html, body, #app, .container {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}

.container {
  height: 500px;
  background-color: rgb(150, 75, 0);
  background-image: url(https://static-cse.canva.com/_next/static/assets/wave_w2881xh801_98aaf3a37b4d733bdb3bc7754c25ad3608c520c5ebb9b1079f2d0c2f78b07ab8.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.video {
  width: 20%;
  height: 70%;
  overflow: hidden;
  border-radius: 10%;
  position: relative;
  margin-right: 5%;
}

.video video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video .overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(150, 75, 0, 0.2);
  border-radius: 10%;
  pointer-events: none; 
}

.titles {
  font-family: 'Montserrat', sans-serif;
  font-size: 20px;
  position: relative;
  bottom: 20%;
}

.titles p {
  color: #EDDEA4;
  font-weight: bold;
}

.p1 {
  position: relative;
  font-size: 30px;
  margin-left: 30px;
}
.p2{
  margin-left: 30px;
}

.factories {
  width: 90%;
  padding: 50px;
  border-radius: 20px;
  background-color: #f2f2f2;
  margin: 40px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.fact-header {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.fact-header button {
  margin-right: auto;
  height: 40px;
  width: 130px;
  font-family: 'Montserrat', sans-serif;
  border-radius: 10px;
  border: 0;
  background-color: #964B00;
  color: white;
}

.fact-header button:hover {
  cursor: pointer;
}

.fact-header .input-container {
  display: flex;
  align-items: center;
  border: 0.2px solid #964B00;
  background-color: white;
  border-radius: 10px;
  padding: 0.7%;
  margin-left: 10px;
}

.fact-header .input-container i {
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.fact-header input {
  border: none;
  outline: none;
  padding: 0;
  flex-grow: 1;
  font-family: 'Montserrat', sans-serif;
}

.fact-header input::placeholder {
  color: #888;
}

.cards {
  width: 100%;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.card {
  width: 20%;
  height: 250px;
  background-color: #C46200;
  justify-content: flex-start;
  margin: 10px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.card:hover{
  cursor: pointer;
}

.card-header {
  align-self: flex-start;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 10px;
}

.card-header img {
  width: 25px;
  height: 25px;
}

.card-header h3 {
  font-size: 1.2em;
  color: #EDDEA4;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
  margin: 0;
}

.card-location {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 30%;
  font-family: 'Montserrat', sans-serif;
  color: #EDDEA4;
}

.card-location p {
  margin-left: 5px;
  font-size: 15px;
  font-family: 'Montserrat', sans-serif;
  color: #EDDEA4;
}

.footer {
  width: 100%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: auto; 
}

.rate {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rate p {
  color: #EDDEA4;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
}

.footer .statusW {
  color: green;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
}

.footer .statusN {
  color: brown;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
}

.filter-div {
  display: none;
  width: 90%;
  padding: 20px;
  background-color: #C46200;;
  border-radius: 20px;
  margin: 20px auto;
}

.sort-header, .filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0 0 0;
  border-bottom: 1px solid #EDDEA4;
}

.sort-p, .filter-p {
  font-size: 18px;
  font-weight: bold;
  font-family: 'Montserrat', sans-serif;
  color:#EDDEA4;
}

.sort-div {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  color:#EDDEA4;
  margin:10px;
  font-family: 'Montserrat', sans-serif;
}

.sort-option {
  margin-bottom: 15px;
  text-align: center;
  width: 100%;
}

.sort-option label, .rating-container label, .chocolate-kinds label, .chocolate-types label {
  margin-right: 10px;
  font-weight: bold;
  font-family: 'Montserrat', sans-serif;
}

.rating-container input, .chocolate-kinds input, .chocolate-types input {
  margin-right: 5px;
}

.filter-work-div {
  padding: 20px 0;
}

.search-container, .rating-container, .chocolate-kinds, .chocolate-types, .show-opened {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px 0;
  font-family: 'Montserrat', sans-serif;
  color:#EDDEA4;
}

.search-container input, .rating-container input {
  border: 0.2px solid #964B00;
  background-color: white;
  border-radius: 10px;
  padding: 0.7%;
  margin:10px;
  font-family: 'Montserrat', sans-serif;
}

.show-opened input {
  margin-right: 10px;
}

.show-opened {
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Montserrat', sans-serif;
}

.deleteFactory{
  background-color: transparent;
  border: 0;
  width: 25px;
  height: 25px;
  cursor: pointer;
  font-family: 'Montserrat', sans-serif;
  font-weight: bold;
  color:#EDDEA4;
}
</style>
