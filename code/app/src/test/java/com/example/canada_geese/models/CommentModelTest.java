package com.example.canada_geese.models;

import static org.junit.Assert.*;

import com.example.canada_geese.Models.CommentModel;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class CommentModelTest {

    private static final String TEST_TEXT = "Test comment";
    private static final String TEST_AUTHOR = "testUser";
    private static final String TEST_DOCUMENT_ID = "doc123";
    private static final String TEST_USER_ID = "user456";

    private Date testDate;
    private CommentModel comment;

    @Before
    public void setUp() {
        testDate = new Date();
        comment = new CommentModel(TEST_TEXT, TEST_AUTHOR, testDate);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(TEST_TEXT, comment.getText());
        assertEquals(TEST_AUTHOR, comment.getAuthor());
        assertEquals(testDate, comment.getTimestamp());

        assertNull(comment.getDocumentId());
        assertNull(comment.getUserId());
    }

    @Test
    public void testSetAndGetDocumentId() {
        comment.setDocumentId(TEST_DOCUMENT_ID);
        assertEquals(TEST_DOCUMENT_ID, comment.getDocumentId());

        comment.setDocumentId(null);
        assertNull(comment.getDocumentId());
    }

    @Test
    public void testSetAndGetUserId() {
        comment.setUserId(TEST_USER_ID);
        assertEquals(TEST_USER_ID, comment.getUserId());

        comment.setUserId(null);
        assertNull(comment.getUserId());
    }

    @Test
    public void testSettersAndGetters() {
        String newText = "New text";
        String newAuthor = "New author";
        Date newDate = new Date(System.currentTimeMillis() + 1000);

        comment.setText(newText);
        comment.setAuthor(newAuthor);
        comment.setTimestamp(newDate);

        assertEquals(newText, comment.getText());
        assertEquals(newAuthor, comment.getAuthor());
        assertEquals(newDate, comment.getTimestamp());
    }

    @Test
    public void testNoArgConstructor() {
        CommentModel emptyComment = new CommentModel();

        assertNull(emptyComment.getText());
        assertNull(emptyComment.getAuthor());
        assertNull(emptyComment.getTimestamp());
        assertNull(emptyComment.getDocumentId());
        assertNull(emptyComment.getUserId());
    }

    @Test
    public void testFormattedTimestamp() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(2023, java.util.Calendar.OCTOBER, 1, 15, 45, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date fixedDate = calendar.getTime();

        comment.setTimestamp(fixedDate);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        String expectedFormatted = sdf.format(fixedDate);
        String actualFormatted = sdf.format(comment.getTimestamp());

        assertEquals("Formatted timestamp should match expected format", expectedFormatted, actualFormatted);
    }

}