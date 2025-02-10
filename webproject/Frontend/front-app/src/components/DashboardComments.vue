<template>
  <div class="dashboard-comments">
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
                  <img src="Icons/user2.png" alt="User Icon" class="user-icon">
                  <span class="username">{{ comment.username }}</span>
              </div>
          </div>
          <div v-if="userRole === 'ADMINISTRATOR' || userRole === 'MANAGER'" class="manager-actions">
              <button class="approve-btn" @click="approveComment(comment.id)" :disabled="comment.status === 'APPROVED'">Approve</button>
              <button class="reject-btn" @click="rejectComment(comment.id)" :disabled="comment.status === 'REJECTED'">Reject</button>
              <button class="delete-btn" @click="deleteComment(comment.id)">Delete</button>
          </div>
          <div class="comment-status">Status: {{ comment.status }}</div>
      </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';

const props = defineProps({
userRole: {
  type: String,
  required: true
}
});

const comments = ref([]);
const commentsWithUsernames = ref([]);

const fetchFactories = async () => {
try {
  const factoryPromises = comments.value.map(comment =>
    axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactories/${comment.factoryId}`)
  );
  const factoryResponses = await Promise.all(factoryPromises);
  commentsWithUsernames.value = commentsWithUsernames.value.map((comment, index) => ({
    ...comment,
    factoryName: factoryResponses[index].data.name
  }));
} catch (error) {
  console.error('Error fetching factory names:', error);
}
};

const fetchComments = async () => {
try {
  const url = `http://localhost:8080/WebShopAppREST/rest/comments`;
  const response = await axios.get(url, { withCredentials: true });
  comments.value = response.data;
  await fetchUsernames();
  await fetchFactories();
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
} catch (error) {
  console.error('Error fetching usernames:', error);
}
};

const formatDate = (dateString) => {
const date = new Date(dateString);
return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

const approveComment = async (commentId) => {
try {
  await axios.put(`http://localhost:8080/WebShopAppREST/rest/comments/approve/${commentId}`, {}, { withCredentials: true });
  await fetchComments();
} catch (error) {
  console.error('Error approving comment:', error);
}
};

const rejectComment = async (commentId) => {
try {
  await axios.put(`http://localhost:8080/WebShopAppREST/rest/comments/reject/${commentId}`, {}, { withCredentials: true });
  await fetchComments();
} catch (error) {
  console.error('Error rejecting comment:', error);
}
};

const deleteComment = async (commentId) => {
try {
  await axios.delete(`http://localhost:8080/WebShopAppREST/rest/comments/${commentId}`, { withCredentials: true });
  await fetchComments();
} catch (error) {
  console.error('Error deleting comment:', error);
}
};

onMounted(() => {
fetchComments();
});
</script>

<style scoped>
.dashboard-comments {
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

.manager-actions {
margin-top: 10px;
display: flex;
justify-content: center;
gap: 10px;
}

.manager-actions .approve-btn {
background-color: #4CAF50;
color: white;
padding: 5px 10px;
border: none;
border-radius: 4px;
cursor: pointer;
}

.manager-actions .reject-btn {
background-color: #f44336;
color: white;
padding: 5px 10px;
border: none;
border-radius: 4px;
cursor: pointer;
}

.manager-actions .delete-btn {
background-color: #ff9800;
color: white;
padding: 5px 10px;
border: none;
border-radius: 4px;
cursor: pointer;
}

.manager-actions .delete-btn:hover {
background-color: #f57c00;
}

.manager-actions button:disabled {
opacity: 0.5;
cursor: not-allowed;
}

.comment-status {
margin-top: 5px;
font-style: italic;
color: #666;
}
</style>