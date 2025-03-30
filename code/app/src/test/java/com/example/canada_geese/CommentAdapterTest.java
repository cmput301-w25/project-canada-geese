package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

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

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CommentAdapterTest {

    @Mock
    private Context mockContext;

    private CommentAdapter adapter;
    private List<CommentModel> commentList;
    private String moodEventId = "test_mood_event_id";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create test comment data
        commentList = new ArrayList<>();
        commentList.add(new CommentModel("First comment", "User1", new Date()));
        commentList.add(new CommentModel("Second comment", "User2", new Date()));

        // Initialize adapter
        adapter = new CommentAdapter(commentList, mockContext, moodEventId);
    }

    @Test
    public void testUpdateComments() {
        // Verify initial comments
        List<CommentModel> initialComments = getCommentList(adapter);
        assertEquals(2, initialComments.size());
        assertEquals("First comment", initialComments.get(0).getText());
        assertEquals("Second comment", initialComments.get(1).getText());

        // Create new comments list
        List<CommentModel> newComments = new ArrayList<>();
        newComments.add(new CommentModel("New comment 1", "User3", new Date()));
        newComments.add(new CommentModel("New comment 2", "User4", new Date()));
        newComments.add(new CommentModel("New comment 3", "User5", new Date()));

        // Update comments list
        adapter.updateComments(newComments);

        // Verify comments were updated
        List<CommentModel> updatedComments = getCommentList(adapter);
        assertEquals(3, updatedComments.size());
        assertEquals("New comment 1", updatedComments.get(0).getText());
        assertEquals("New comment 2", updatedComments.get(1).getText());
        assertEquals("New comment 3", updatedComments.get(2).getText());
        assertEquals("User3", updatedComments.get(0).getAuthor());
        assertEquals("User4", updatedComments.get(1).getAuthor());
        assertEquals("User5", updatedComments.get(2).getAuthor());
    }

    // Helper method to access the comment list using reflection
    private List<CommentModel> getCommentList(CommentAdapter adapter) {
        try {
            Field field = CommentAdapter.class.getDeclaredField("commentList");
            field.setAccessible(true);
            return (List<CommentModel>) field.get(adapter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access commentList field", e);
        }
    }
}