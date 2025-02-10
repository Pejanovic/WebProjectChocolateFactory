<template>

</template>

<script setup>




onMounted(() => {
    console.log("Initial mode:", mode.value);
    loadChocolates();
});
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

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

.choco-form {
    display: flex;
    flex-direction: column;
    max-width: 400px;
    margin: auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 10px;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;
}

.choco-form label {
    margin-bottom: 5px;
    font-weight: bold;
}

.choco-form input,
.choco-form select,
.choco-form textarea {
    margin-bottom: 15px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-family: 'Roboto', sans-serif;
}

.choco-form input[type="submit"] {
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 20px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.choco-form input[type="submit"]:hover {
    background-color: #45a049;
}

.choco-form textarea {
    resize: vertical;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

th {
    background-color: #f2f2f2;
}

.chocolate-list {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap; 
    margin: 10px;
}

.image {
    width: calc(100% + 20px); 
    height: 30%;
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
    margin: 10px;
}

.choco-detail .icon {
    margin-right: 5px;
    width: 25px;
    height: 25px;
}

.choco-desc {
    height: 90px;
    margin: 10px;
    overflow: hidden;
    text-overflow: ellipsis; 
}


p{
    font-family: 'Roboto', sans-serif;
}

h3{
    font-family: 'Roboto', sans-serif;
    font-weight: bold;
}

.choco-button {
    background: transparent;
    border: 1px solid black;
    border-radius: 50%;
    margin: 10px;
    padding: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); 
    transition: box-shadow 0.3s ease-in-out; 
}

.choco-button:hover {
    cursor: pointer;
    box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.3); 
}
.choco-button img{
    width: 20px;
}

.chocolate-item {
    border: 1px solid black;
    border-radius: 10%;
    margin: 10px;
    width: 200px; 
    height: 300px; 
    overflow: hidden; 
    display: flex;
    flex-direction: column; 
    padding: 10px;
}
</style>
