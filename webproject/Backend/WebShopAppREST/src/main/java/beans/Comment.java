package beans;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private int id;
    private int customerId;
    private int factoryId;
    private String text;
    private int rating;
    private CommentStatusEnum status;
    private LocalDateTime commentTime;


    public Comment(int id, int customerId, int factoryId, String text, int rating) {
        this.id = id;
        this.customerId = customerId;
        this.factoryId = factoryId;
        this.text = text;
        this.rating = rating;
        this.status = CommentStatusEnum.PENDING;
        this.commentTime = LocalDateTime.now();
    }
    
    public Comment(int id, int customerId, int factoryId, String text, int rating, CommentStatusEnum status, LocalDateTime commentTime) {
        this.id = id;
        this.customerId = customerId;
        this.factoryId = factoryId;
        this.text = text;
        this.rating = rating;
        this.status = status != null ? status : CommentStatusEnum.PENDING;
        this.commentTime = commentTime != null ? commentTime : LocalDateTime.now();
    }
    
    public Comment() {
        // Postavljanje default vrijednosti ako je potrebno
        this.status = CommentStatusEnum.PENDING;
        this.commentTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public CommentStatusEnum getStatus() {
        return status;
    }

    public LocalDateTime getCommentTime() {
        return commentTime;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setStatus(CommentStatusEnum status) {
        this.status = status;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, factoryId, text, rating, status, commentTime);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Comment comment = (Comment) obj;
        return id == comment.id &&
               customerId == comment.customerId &&
               factoryId == comment.factoryId &&
               rating == comment.rating &&
               Objects.equals(text, comment.text) &&
               status == comment.status &&
               Objects.equals(commentTime, comment.commentTime);
    }


    @Override
    public String toString() {
        return "Comment{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", factoryId=" + factoryId +
               ", text='" + text + '\'' +
               ", rating=" + rating +
               ", status=" + status +
               ", commentTime=" + commentTime +
               '}';
    }
}