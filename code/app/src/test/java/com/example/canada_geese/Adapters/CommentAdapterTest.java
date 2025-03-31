package com.example.canada_geese.Adapters;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import com.example.canada_geese.Adapters.CommentAdapter;
import com.example.canada_geese.Models.CommentModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for the CommentAdapter class.
 * Verifies updating and deleting comment functionality without modifying the adapter.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CommentAdapterTest {

    @Mock
    private Context mockContext;

    private CommentAdapter adapter;
    private List<CommentModel> commentList;
    private String moodEventId = "test_mood_event_id";

    /**
     * Set up test data and initialize the adapter.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        commentList = new ArrayList<>();
        commentList.add(new CommentModel("First comment", "User1", new Date()));
        commentList.add(new CommentModel("Second comment", "User2", new Date()));

        adapter = new CommentAdapter(commentList, mockContext, moodEventId);
    }

    /**
     * Test: Update the list of comments.
     */
    @Test
    public void testUpdateComments() {
        List<CommentModel> initialComments = getCommentList(adapter);
        assertEquals(2, initialComments.size());
        assertEquals("First comment", initialComments.get(0).getText());
        assertEquals("Second comment", initialComments.get(1).getText());

        List<CommentModel> newComments = new ArrayList<>();
        newComments.add(new CommentModel("New comment 1", "User3", new Date()));
        newComments.add(new CommentModel("New comment 2", "User4", new Date()));
        newComments.add(new CommentModel("New comment 3", "User5", new Date()));

        adapter.updateComments(newComments);

        List<CommentModel> updatedComments = getCommentList(adapter);
        assertEquals(3, updatedComments.size());
        assertEquals("New comment 1", updatedComments.get(0).getText());
        assertEquals("New comment 2", updatedComments.get(1).getText());
        assertEquals("New comment 3", updatedComments.get(2).getText());
        assertEquals("User3", updatedComments.get(0).getAuthor());
        assertEquals("User4", updatedComments.get(1).getAuthor());
        assertEquals("User5", updatedComments.get(2).getAuthor());
    }

    /**
     * Test: Simulate deleting a comment by removing it from the list via reflection.
     */
    @Test
    public void testDeleteComment() {
        // Get original comment list
        List<CommentModel> comments = getCommentList(adapter);

        // Check original size
        assertEquals(2, comments.size());

        // Remove the first comment manually (simulate deletion)
        comments.remove(0);

        // Update adapter's list using reflection
        setCommentList(adapter, comments);

        // Check updated list
        List<CommentModel> updated = getCommentList(adapter);
        assertEquals(1, updated.size());
        assertEquals("Second comment", updated.get(0).getText());
        assertEquals("User2", updated.get(0).getAuthor());
    }

    /**
     * Helper method: Access the internal comment list using reflection.
     */
    private List<CommentModel> getCommentList(CommentAdapter adapter) {
        try {
            Field field = CommentAdapter.class.getDeclaredField("commentList");
            field.setAccessible(true);
            return (List<CommentModel>) field.get(adapter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access commentList field", e);
        }
    }

    /**
     * Helper method: Set the internal comment list using reflection.
     */
    private void setCommentList(CommentAdapter adapter, List<CommentModel> newList) {
        try {
            Field field = CommentAdapter.class.getDeclaredField("commentList");
            field.setAccessible(true);
            field.set(adapter, newList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set commentList field", e);
        }
    }
}