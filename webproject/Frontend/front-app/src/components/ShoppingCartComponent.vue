<template>
    <h1 class="title">Shopping cart</h1>
    <div class="shopping-cart">
        <div class="success">
            <p class="success-p">{{ success }}</p>
        </div>
        <div class="choco" v-for="shopp in shoppings" :key="shopp.id">
            <img :src="shopp.chocolate.image || 'chocolate.jpeg'" alt="chocolateImage" class="img">
            <div class="choco-info">
                <p class="name">{{ shopp.chocolate.name }}</p>
                <div class="type">
                    <p>{{ shopp.chocolate.category }} - {{ shopp.chocolate.type }}</p>
                    <p>{{ shopp.chocolate.grammage }}g</p>
                </div>
            </div>
            <div class="amounts-div">
                 <button class="button-plus" @click="updateCardplus(shopp)">+</button>
                 <p class="amounts">{{ shopp.amount }}</p>
                 <button class="button-minus" @click="updateCardminus(shopp)">-</button>
            </div>
            <div class="priceChoco-div">
                <div class="din">
                    <p class="amounts">{{ shopp.chocolate.price * shopp.amount }}</p>
                    <p class="price-din">din</p>
                </div>
                 <button class="button-remove" @click="removeItem(shopp.chocolate.id)">Remove</button>
            </div>
        </div>
        <div class="container-price" v-if="shoppings.length > 0">
            <div class="full-price">
                <p>{{ fullprice }}din</p>
            </div>
        </div>
    </div>
    <div class="button-submit">
        <button class="submit" @click="createCard">Buy</button>
    </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const username = sessionStorage.getItem('username') || 'defaultUser';
const type = sessionStorage.getItem('type');
const session = sessionStorage.getItem('sessionId');
const user = ref({});
const fullprice = ref(0);

const shoppings = ref([]);
var success = ref("");

const newShoppingRequest = ref({
    factoryId: 0,
    price: 0.0, 
    nameUser: username,
    status: 'PROCESSING', 
    chocolates: [],
    amounts: [],
    date: new Date().toISOString() 
});

const fetchUser = async (username) => {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
    user.value = response.data;
    console.log(user.value)
  } catch (error) {
    console.error("Došlo je do greške prilikom preuzimanja korisnika", error);
  }
};

const fetchCards = async () => {
    try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/cart/items', {
            withCredentials: true
        });
        shoppings.value = response.data;
        console.log(shoppings);
        calculateFullPrice();
        if (shoppings.value.length > 0) {
            newShoppingRequest.value.factoryId = shoppings.value[0].chocolate.factoryId;
        }
    } catch (error) {
        console.error('Greška prilikom preuzimanja stavki iz korpe:', error);
    }
};

const calculateFullPrice = () => {
    if (!shoppings.value || !Array.isArray(shoppings.value) || shoppings.value.length === 0) {
        return;
    }

    fullprice.value = shoppings.value.reduce((total, shopp) => total + (shopp.chocolate.price * shopp.amount), 0);

    if (user.value.customerType === 'GOLD') {
        fullprice.value *= 0.90;
    } else if (user.value.customerType === 'SILVER') {
        fullprice.value *= 0.85;
    }

    fullprice.value = parseFloat(fullprice.value.toFixed(2));

    newShoppingRequest.value.price = fullprice.value;
};



const updateCardplus = async (shopp) => {
    const { amount, chocolate } = shopp;
    const { id, quantity } = chocolate;

    if (amount < quantity) {
        try {
            const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/cart/items`, null, {
                params: { chocolateId: id, amount: amount + 1 },
                withCredentials: true 
            });
            shoppings.value = response.data;
            calculateFullPrice();
        } catch (error) {
            console.error('Greška prilikom ažuriranja stavke u korpi:', error);
        }
    } else {
        console.log('Količina proizvoda je dostigla maksimalnu dostupnu količinu.');
    }
};


const updateCardminus = async (shopp) => {
    if (shopp.amount > 1) { 
        try {
            const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/cart/items?chocolateId=${shopp.chocolate.id}&amount=${shopp.amount - 1}`, {}, {
                withCredentials: true 
            });
            shoppings.value = response.data;
            calculateFullPrice();
        } catch (error) {
            console.error('Greška prilikom ažuriranja stavke u korpi:', error);
        }
    }
};

const createCard = async () => {
    try {
        newShoppingRequest.value.chocolates = shoppings.value.map(shopp => shopp.chocolate);
        newShoppingRequest.value.amounts = shoppings.value.map(shopp => shopp.amount);
        
        console.log('Creating shopping request:', newShoppingRequest.value);
        
        const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/shoppings', newShoppingRequest.value, {
            withCredentials: true
        });
        console.log('Shopping request created successfully', response);
        for (const shopp of shoppings.value) {
            await removeItem(shopp.chocolate.id);
        }

        success.value = "Your purchase was successfully added, and it is currently being processed.";
    } catch (err) {
        console.error('There was an error creating the shopping request', err);
    }
};


const removeItem = async (shoppId) => {
    try {
        const response = await axios.delete(`http://localhost:8080/WebShopAppREST/rest/cart/items`, {
            params: {
                chocolateId: shoppId,
                sessionId: session
            },
            withCredentials: true
        });
        shoppings.value = shoppings.value.filter(shopp => shopp.id !== shoppId);
        calculateFullPrice();
        fetchCards();
    } catch (error) {
        console.error('Greška prilikom uklanjanja stavke iz korpe:', error);
    }
};

onMounted(() => {
    fetchCards();
    fetchUser(username);
});
</script>

<style>
.title {
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    margin: 10px;
}

.success{
    text-align: center;
}

.success-p{
    font-family: 'Montserrat', sans-serif;
    color: green;
    font-weight: bold;
}

.shopping-cart {
    width: 90%;
    justify-content: center;
    margin: 0 auto;
    background-color: #fafafa;
    border-radius: 20px;
    padding: 20px;
}

.choco {
    display: flex;
    border-bottom: 1px solid #EDDEA4;
    align-items: center;
    padding: 10px;
    font-family: 'Montserrat', sans-serif;
}

.img {
    width: 60px;
    height: 60px;
    margin-right: 10px;
}

.name {
    font-weight: bold;
    font-size: 20px;
}

.choco-info {
    margin-right: 20px;
    display: block;
}

.type {
    display: block;
    line-height: 2px;
}
.button-submit {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    margin: 20px;
}

.submit {
    text-align: center;
    padding: 15px 35px;
    transition: padding 0.3s ease;
    font-family: 'Montserrat', sans-serif;
    background-color: #EDDEA4;
    color: white;
    border: none;
    cursor: pointer;
}

.submit:hover{
    padding: 20px 50px;
}


.amounts-div {
    display: flex;
    align-items: center;
    margin-right: 20px;
    flex: 1;
    justify-content: center;
}

.amounts-div .button-plus,
.amounts-div .button-minus {
    width: 30px;
    height: 30px;
    font-size: 18px;
    margin: 0 20px;
    background-color: transparent;
    border: 1px solid black;
    border-radius: 50%;
}

.amounts-div .button-plus:hover,
.amounts-div .button-minus:hover {
    cursor: pointer;
}

.priceChoco-div {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
}

.din {
    display: flex;
    align-items: baseline;
    white-space: nowrap;
}

.din p {
    margin: 0;
}

.button-remove {
    color: red;
    background-color: transparent;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
}

.container-price {
    display: flex;
    justify-content: flex-end;
    width: 100%;
}

.full-price {
    padding: 10px auto;
    border: 1px solid black;
    font-family: 'Montserrat', sans-serif;
    text-align: center;
    width: 10%;
    margin: 20px 0 20px auto;
}

</style>
