package com.example.canada_geese.Models;

import java.util.Date;

public class CommentModel {
    private String text;
    private String author;
    private Date timestamp;
    private String userId;     // The UID of the user who posted the comment
    private String documentId; // The Firestore document ID for this comment

    public CommentModel() {
        // No-argument constructor required for Firestore
    }

    public CommentModel(String text, String author, Date timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}