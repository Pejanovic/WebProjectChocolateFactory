<template>
  <div class="comment-list">
    <div v-for="comment in commentsWithUsernames" :key="comment.id" class="comment-item">
      <div class="comment-rating">
        <span v-for="star in comment.rating" :key="star">★</span>
      </div>
      <div class="comment-text">{{ comment.text }}</div>
      <div class="comment-footer">
        <div class="left-section">
          <img src="Icons/factory.png" alt="Factory Icon" class="factory-icon">
          <span class="factory-name">{{ comment.factoryName }}</span>
        </div>
        <span class="comment-date">{{ formatDate(comment.commentTime) }}</span>
        <div class="right-section">
          <img src="Icons/user.png" alt="User Icon" class="user-icon">
          <span class="username">{{ comment.username }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';

const props = defineProps({
  factoryId: {
    type: Number,
    default: null
  }
});

const comments = ref([]);
const commentsWithUsernames = ref([]);
const user = ref(null);

const fetchUserAndComments = async () => {
  try {
    const username = sessionStorage.getItem('username');
    const userResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/user/username?username=${username}`, { withCredentials: true });
    user.value = userResponse.data;
    console.log("User fetched successfully:", user.value);
    fetchComments();
  } catch (error) {
    console.error("Došlo je do greške prilikom preuzimanja korisnika", error);
  }
};

const fetchComments = async () => {
  try {
    const url = `http://localhost:8080/WebShopAppREST/rest/comments/FactorySearch?id=${props.factoryId}&role=${user.value.userType}`;
    const response = await axios.get(url);
    comments.value = response.data;
    console.log("Comments fetched successfully:", comments.value);
    await fetchUsernames();
  } catch (error) {
    console.error('Error fetching comments:', error);
  }
};

const fetchUsernames = async () => {
  try {
    const promises = comments.value.map(comment => 
      axios.get(`http://localhost:8080/WebShopAppREST/rest/user/userById/${comment.customerId}`)
    );
    const responses = await Promise.all(promises);
    commentsWithUsernames.value = comments.value.map((comment, index) => ({
      ...comment,
      username: responses[index].data.username
    }));
    console.log("Comments with usernames:", commentsWithUsernames.value);
  } catch (error) {
    console.error('Error fetching usernames:', error);
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

onMounted(() => {
  fetchUserAndComments();
});
</script>


<style scoped>
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.comment-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80%;
}

.comment-rating {
  display: flex;
  justify-content: center;
  font-size: 18px;
  color: #ffd700;
}

.comment-text {
  margin: 10px 0;
  text-align: center;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.left-section, .right-section {
  display: flex;
  align-items: center;
}

.factory-icon, .user-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.factory-name, .username {
  font-weight: bold;
  margin-right: 10px;
}

.comment-date {
  color: #666;
  margin-right: 10px;
}
</style>
