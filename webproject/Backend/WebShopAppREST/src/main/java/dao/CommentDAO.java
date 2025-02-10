package dao;

import beans.Comment;
import beans.CommentStatusEnum;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CommentDAO {

    private HashMap<Integer, Comment> comments = new HashMap<>();

    public CommentDAO() {
    	
    }

    public CommentDAO(String contextPath) {
        loadComments(contextPath);
    }

    public Collection<Comment> getAll() {
        return comments.values();
    }

    public Collection<Comment> getAllByFactoryId(int factoryId) {
        return comments.values().stream()
                .filter(comment -> comment.getFactoryId() == factoryId)
                .collect(Collectors.toList());
    }

    public Collection<Comment> getAllByCustomerId(int customerId) {
        return comments.values().stream()
                .filter(comment -> comment.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    public Comment getById(int id) {
        return comments.get(id);
    }

    public Comment saveComment(String contextPath, Comment comment) {
        if (!isValidComment(comment)) {
        	System.out.println("Invalid comment: " + comment);
            throw new IllegalArgumentException("Invalid comment");
        }

        int newId = comments.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        comment.setId(newId);
        comment.setCommentTime(LocalDateTime.now());
        comments.put(newId, comment);
        saveAllComments(contextPath);

        return comment;
    }
    

    public Comment updateComment(int id, Comment updatedComment, String contextPath) {
        if (comments.containsKey(id) && isValidComment(updatedComment)) {
            updatedComment.setId(id);
            comments.put(id, updatedComment);
            saveAllComments(contextPath);
            return updatedComment;
        }
        return null;
    }

    public Comment deleteComment(int id, String contextPath) {
        Comment removedComment = comments.remove(id);
        if (removedComment != null) {
            saveAllComments(contextPath);
        }
        return removedComment;
    }

    private void saveAllComments(String contextPath) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(contextPath + "/comments.txt")))) {
            for (Comment comment : comments.values()) {
                out.write(comment.getId() + ";" + comment.getCustomerId() + ";" + comment.getFactoryId() + ";" +
                          comment.getText() + ";" + comment.getRating() + ";" + comment.getStatus().name() + ";" +
                          comment.getCommentTime() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadComments(String contextPath) {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(contextPath + "/comments.txt")))) {
            String line;
            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() != 7) continue;

                int id = Integer.parseInt(st.nextToken().trim());
                int customerId = Integer.parseInt(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                String text = st.nextToken().trim();
                int rating = Integer.parseInt(st.nextToken().trim());
                CommentStatusEnum status = CommentStatusEnum.valueOf(st.nextToken().trim());
                LocalDateTime commentTime = LocalDateTime.parse(st.nextToken().trim());

                Comment comment = new Comment(id, customerId, factoryId, text, rating);
                comment.setStatus(status);
                comment.setCommentTime(commentTime);

                comments.put(id, comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Comment approveComment(int id, String contextPath) {
        Comment comment = comments.get(id);
        if (comment != null && comment.getStatus() == CommentStatusEnum.REJECTED) {
            comment.setStatus(CommentStatusEnum.APPROVED);
            saveAllComments(contextPath);
            return comment;
        }
        return null;
    }

    public Comment rejectComment(int id, String contextPath) {
        Comment comment = comments.get(id);
        if (comment != null && comment.getStatus() == CommentStatusEnum.APPROVED) {
            comment.setStatus(CommentStatusEnum.REJECTED);
            saveAllComments(contextPath);
            return comment;
        }
        return null;
    }
    
    public boolean isValidComment(Comment comment) {
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            return false;
        }
        if (comment.getRating() < 1 || comment.getRating() > 5) {
            return false;
        }
        return true;
    }
}
