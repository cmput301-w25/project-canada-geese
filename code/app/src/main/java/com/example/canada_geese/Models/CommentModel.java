package com.example.canada_geese.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a comment on a mood event including content, author info,
 * timestamp, user ID, and Firestore document metadata.
 */
public class CommentModel {
    private String text;
    private String author;
    private Date timestamp;
    private String userId;
    private String documentId;

    /**
     * No-argument constructor required for Firestore.
     */
    public CommentModel() {}

    /**
     * Constructs a comment with the specified text, author, and timestamp.
     *
     * @param text the comment text
     * @param author the comment author username
     * @param timestamp the time the comment was made
     */
    public CommentModel(String text, String author, Date timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    /**
     * Returns the comment text.
     * @return the comment text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the comment text.
     * @param text the new comment text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the comment author.
     * @return the author's username
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the comment author.
     * @param author the author's username
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the timestamp of the comment.
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the comment.
     * @param timestamp the time the comment was made
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the user ID of the comment's author.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID of the comment's author.
     * @param userId the Firebase UID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the Firestore document ID.
     * @return the comment document ID
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the Firestore document ID.
     * @param documentId the comment's document ID
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Converts this comment to a Firestore-compatible map.
     * @return a map representation of this comment
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("author", author);
        map.put("timestamp", timestamp);
        map.put("userId", userId);
        return map;
    }

    /**
     * Returns a string representation of this comment.
     * @return a string representation of the comment
     */
    @Override
    public String toString() {
        return "CommentModel{" +
                "text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}
