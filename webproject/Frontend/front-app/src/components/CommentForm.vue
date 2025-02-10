<template>
    <div class="comment-form-container">
      <div class="comment-form">
        <h3>Leave a Comment</h3>
        <form @submit.prevent="submitComment">
          <textarea v-model="commentText" placeholder="Enter your comment" required></textarea>
          <div class="rating">
            <label>Rating:</label>
            <select v-model="rating" required>
              <option value="1">1</option>
              <option value="2">2</option>
              <option value="3">3</option>
              <option value="4">4</option>
              <option value="5">5</option>
            </select>
          </div>
          <button type="submit">Submit Comment</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    props: ['factoryId'],
    data() {
      return {
        commentText: '',
        rating: 5
      }
    },
    methods: {
      async submitComment() {
        const username = sessionStorage.getItem('username');
        if (!username) {
          console.error('Username not found in sessionStorage');
          return;
        }
  
        let customerId = null;
        try {
          const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/idByUsername?username=${username}`);
          customerId = response.data;
        } catch (error) {
          console.error('Error fetching user ID:', error);
          alert('Failed to fetch user ID. Please try again.');
          return;
        }
  
        const commentData = {
          text: this.commentText,
          rating: parseInt(this.rating),
          customerId: customerId,
          factoryId: this.factoryId,
          commentTime: new Date().toISOString()
        };
  
        try {
          await axios.post('http://localhost:8080/WebShopAppREST/rest/comments', commentData);
          this.$emit('comment-submitted');
          this.commentText = '';
          this.rating = 5;
        } catch (error) {
          console.error('Error submitting comment:', error);
          alert('Failed to submit comment. Please try again.');
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .comment-form-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .comment-form {
    width: 100%;
    max-width: 500px;
    margin: 20px;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f9f9f9;
  }
  
  h3 {
    margin-bottom: 15px;
    color: #4CAF50; 
    text-align: center;
  }
  
  textarea {
    width: 100%;
    height: 100px;
    margin-bottom: 15px;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 14px;
  }
  
  .rating {
    margin-bottom: 15px;
    display: flex;
    align-items: center;
  }
  
  .rating label {
    margin-right: 10px;
    font-weight: bold;
    color: #333;
  }
  
  .rating select {
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }
  
  button {
    background-color: #4CAF50; 
    color: white;
    border: none;
    padding: 10px 15px;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    width: 100%;
  }
  
  button:hover {
    background-color: #45a049;
  }
  
  button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  </style>
  