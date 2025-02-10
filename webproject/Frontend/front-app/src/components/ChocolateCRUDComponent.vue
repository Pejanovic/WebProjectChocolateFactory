<template>
    <div class="add-chocolate-div" v-show="!isEditing">
        <form @submit.prevent="submitChocolate" class="add-chocolate-form">
            <h2>Add New Chocolate</h2>
            <label for="choco-name">Chocolate Name:</label>
            <input type="text" id="choco-name" v-model="newChocolate.name" required>

            <label for="choco-price">Price:</label>
            <input type="number" id="choco-price" v-model="newChocolate.price" step="0.1" required>

            <label for="choco-grammage">Grammage:</label>
            <input type="number" id="choco-grammage" v-model="newChocolate.grammage" step="0.1" required>

            <label for="choco-description">Description:</label>
            <textarea id="choco-description" v-model="newChocolate.description" required></textarea>

            <label for="choco-category">Category:</label>
            <select id="choco-category" v-model="newChocolate.category" required>
                <option value="DARK">Dark</option>
                <option value="MILK">Milk</option>
                <option value="WHITE">White</option>
            </select>

            <label for="choco-type">Type:</label>
            <select id="choco-type" v-model="newChocolate.type" required>
                <option value="REGULAR">Regular</option>
                <option value="FOR_COOKING">For Cooking</option>
                <option value="FOR_DRINKING">For Drinking</option>
            </select>

            <label for="choco-status">Status:</label>
            <select id="choco-status" v-model="newChocolate.status" required>
                <option value="OUT_OF_STOCK">Out of Stock</option>
            </select>

            <label for="choco-image">Image:</label>
            <input type="file" id="choco-image" @change="onFileChange" required>
            <div v-if="imageUrl" class="image-preview">
                <p>Image Preview:</p>
                <img :src="imageUrl" alt="Preview" />
            </div>

            <div class="button-container-chocolate-add">
                <button type="submit">Add Chocolate</button>
            </div>
        </form>
    </div>

    <div class="update-chocolate-div" v-show="isEditing">
        <form @submit.prevent="updateChocolate" class="add-chocolate-form">
            <h2>Update Chocolate</h2>
            <label for="edit-choco-name">Chocolate Name:</label>
            <input type="text" id="edit-choco-name" v-model="editChocolate.name" required>

            <label for="edit-choco-price">Price:</label>
            <input type="number" id="edit-choco-price" v-model.number="editChocolate.price" step="0.1" required>

            <label for="edit-choco-grammage">Grammage:</label>
            <input type="number" id="edit-choco-grammage" v-model.number="editChocolate.grammage" required>

            <label for="edit-choco-description">Description:</label>
            <textarea id="edit-choco-description" v-model="editChocolate.description" required></textarea>

            <label for="edit-choco-category">Category:</label>
            <select id="edit-choco-category" v-model="editChocolate.category" required>
                <option value="DARK">Dark</option>
                <option value="MILK">Milk</option>
                <option value="WHITE">White</option>
            </select>

            <label for="edit-choco-type">Type:</label>
            <select id="edit-choco-type" v-model="editChocolate.type" required>
                <option value="REGULAR">Regular</option>
                <option value="FOR_COOKING">For Cooking</option>
                <option value="FOR_DRINKING">For Drinking</option>
            </select>

            <label for="edit-choco-status">Status:</label>
            <select id="edit-choco-status" v-model="editChocolate.status" required>
                <option value="IN_STOCK">In Stock</option>
                <option value="OUT_OF_STOCK">Out of Stock</option>
            </select>

            <label for="edit-choco-image">Image:</label>
            <input type="file" id="edit-choco-image" @change="onEditFileChange">
            <div v-if="editImageUrl" class="image-preview">
                <p>Image Preview:</p>
                <img :src="editImageUrl" alt="Preview" />
            </div>

            <div class="button-container-chocolate-update-delete">
                <button type="submit">Update Chocolate</button>
                <button @click="cancelEdit">Cancel</button>
            </div>
        </form>
    </div>

    <div class="chocolate-list">
      <div v-for="choco in chocolates" :key="choco.id" class="chocolate-item">
        <img :src="choco.image" alt="Chocolate Image" class="image">
        <h3>{{ choco.name }}</h3>
        <div class="choco-info">
          <p class="choco-detail"><img src="Icons/dollar.png" class="icon"> {{ choco.price }} din</p>
          <p class="choco-detail"><img src="Icons/weight.png" class="icon"> {{ choco.grammage }}g</p>
        </div>
        <p class="choco-desc">Description: {{ choco.description }}</p>
        <div class="footer-statuss">
          <p>{{ choco.status }}</p>
          <p>{{ choco.category }} - {{ choco.type }}</p>
        </div>
        <div class="buttons-container">
            <button class="update-button-choco" @click="startEdit(choco)">Update</button>
            <button class="delete-button-choco" @click="deleteChocolate(choco.id)">Delete</button>
        </div>
      </div>
    </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';

const chocolates = ref([]);
const username = sessionStorage.getItem('username');
const user = ref({});
const imageUrl = ref('');
const editImageUrl = ref('');
const isEditing = ref(false);
const editChocolate = ref({});
const newChocolate = ref({
    name: '',
    price: 0.0,
    grammage: 0.0,
    factoryId: user.value.factoryId,
    description: '',
    category: '',
    type: '',
    status: '',
    quantity: 0,
    image: ''
});

const fetchUser = async () => {
    try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
        user.value = response.data;

        newChocolate.value.factoryId = user.value.factoryId;
        fetchChocolates();
    } catch (error) {
        console.error("Failed to retrieve user data", error);
    }
};

onMounted(() => {
    fetchUser();
});

const fetchChocolates = () => {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/FactorySearch?id=${user.value.factoryId}`)
        .then(response => {
            chocolates.value = response.data.map(choco => {
                const fullPath = choco.image;
                const fileName = fullPath.split('\\').pop();
                choco.image = fileName;
                return choco;
            });
        })
        .catch(error => {
            console.error('Failed to fetch chocolates', error);
        });
}


const onFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            imageUrl.value = e.target.result;
            newChocolate.value.image = e.target.result.split(',')[1];
        };
        reader.readAsDataURL(file);
    }
};

const startEdit = (choco) => {
    isEditing.value = true;
    editChocolate.value = { ...choco };

    if (choco.image) {
        const fullPath = choco.image;
        const fileName = fullPath.split('\\').pop();
        editImageUrl.value = fileName;
    } else {
        editImageUrl.value = `download.jpeg`;
    }
};

const onEditFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            editImageUrl.value = e.target.result;
            editChocolate.value.image = e.target.result.split(',')[1];
        };
        reader.readAsDataURL(file);
    } else {
        editImageUrl.value = '';
    }
};

const submitChocolate = async () => {
    if (!newChocolate.value.name || !newChocolate.value.price || !newChocolate.value.grammage ||
        !newChocolate.value.description || !newChocolate.value.category || !newChocolate.value.type ||
        !newChocolate.value.status || !newChocolate.value.image) {
        alert("All fields are required");
        return;
    }

    try {
        const imagePayload = {
            image: newChocolate.value.image,
            name: newChocolate.value.name
        };

        const imageResponse = await axios.post('http://localhost:8080/WebShopAppREST/rest/imageupload/chocolate', imagePayload, {
            withCredentials: true
        });
        newChocolate.value.image = imageResponse.data;
        newChocolate.value.quantity = 0;

        const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates', newChocolate.value, {
            withCredentials: true
        });
        chocolates.value.push(response.data);
        fetchChocolates();
        console.log('Successfully added chocolate with image path', response.data);
    } catch (error) {
        if (error.response && error.response.config.url.includes('imageupload')) {
            console.error('Error uploading image:', error);
        } else if (error.response && error.response.config.url.includes('chocolates')) {
            console.error('Error adding chocolate:', error);
        } else {
            console.error('An unexpected error occurred:', error);
        }
    }
};


const cancelEdit = () => {
    isEditing.value = false;
    editChocolate.value = {};
    editImageUrl.value = '';
};

const updateChocolate = async () => {
    if (!editChocolate.value.name || !editChocolate.value.price || !editChocolate.value.grammage ||
        !editChocolate.value.description || !editChocolate.value.category || !editChocolate.value.type ||
        !editChocolate.value.status) {
        alert("All fields are required");
        return;
    }

    try {
        let imageChanged = false;
        
        if (editImageUrl.value && editImageUrl.value.startsWith('data:image')) {
            imageChanged = true;
            const imagePayload = {
                image: editChocolate.value.image,
                name: editChocolate.value.name
            };
            const imageResponse = await axios.post('http://localhost:8080/WebShopAppREST/rest/imageupload/chocolate', imagePayload, {
                withCredentials: true
            });
            editChocolate.value.image = imageResponse.data;
        }

        if (!imageChanged) {
            editChocolate.value.image = editChocolate.value.image;
        }

        const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${editChocolate.value.id}`, editChocolate.value, {
            withCredentials: true
        });
        const index = chocolates.value.findIndex(choco => choco.id === editChocolate.value.id);
        chocolates.value.splice(index, 1, response.data);
        console.log('Successfully updated chocolate', response.data);
        cancelEdit();
    } catch (error) {
        console.error('Error updating chocolate:', error);
    }
};

const deleteChocolate = async (id) => {
    try {
        await axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`, {
            withCredentials: true
        });
        chocolates.value = chocolates.value.filter(choco => choco.id !== id);
        console.log('Successfully deleted chocolate');
    } catch (error) {
        console.error('Error deleting chocolate:', error);
    }
};
</script>

<style>
.chocolate-item {
    position: relative;
    border-radius: 30px;
    overflow: hidden;
}

.footer-statuss{
    margin-bottom: 45px;
    display: flex;
    justify-content: space-around;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
    font-size: 10px;
}

.buttons-container {
    display: flex;
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
}

.buttons-container button {
    flex: 1;
    padding: 10px 0;
    border-radius: 0;
    background-color: #EDDEA4;
    font-family: 'Montserrat', sans-serif;
    font-weight: 500;
    cursor: pointer;
    color: white;
}

.buttons-container:hover{
    background-color: #EDDEA4;
}


.update-button {
    border: 0;
    border-right: 1px solid white;
}

.update-chocolate-div{
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.delete-button {
    border: 0;
    border-left: 1px solid white;
}

.add-chocolate-div {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.add-chocolate-form {
    border: 1px solid #ccc;
    padding: 20px;
    border-radius: 10px;
    width: 50%;
    background-color: #f9f9f9;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.add-chocolate-form h2 {
    text-align: center;
    font-family: 'Montserrat', sans-serif;
    color: #333;
}

.add-chocolate-form label {
    display: block;
    margin: 10px 0 5px;
    font-family: 'Montserrat', sans-serif;
    font-size: 16px;
}

.add-chocolate-form input,
.add-chocolate-form select,
.add-chocolate-form textarea {
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-family: 'Montserrat', sans-serif;
}

.button-container-chocolate-update-delete{
    display: flex;
}
.button-container-chocolate-update-delete button{
    margin: 5px;
    padding: 0;
}

.add-chocolate-form button {
    width: 100%;
    padding: 10px;
    background-color: #EDDEA4;
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
}

.image-preview {
    margin-top: 10px;
    text-align: center;
}

.image-preview img {
    width: auto;
    height: 100px;
    border-radius: 5px;
}

.button-container-chocolate-add {
    margin-top: 20px;
}
</style>
