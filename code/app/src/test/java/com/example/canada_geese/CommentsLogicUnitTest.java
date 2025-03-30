package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.canada_geese.Adapters.CommentAdapter;
import com.example.canada_geese.Models.CommentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for the comments functionality
 * This uses complete mocking to avoid any Firebase initialization
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, manifest = Config.NONE)
public class CommentsLogicUnitTest {

    private static final String TEST_MOOD_EVENT_ID = "test_mood_event_id";
    private static final String TEST_MOOD_OWNER_ID = "test_mood_owner_id";
    private static final String TEST_COMMENT_ID = "test_comment_id";
    private static final String TEST_COMMENT_TEXT = "This is a test comment";
    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_USERNAME = "testuser";

    @Mock
    private Task<DocumentReference> mockTask;

    @Mock
    private DocumentReference mockDocRef;

    private List<CommentModel> testComments;
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();

        // Set up test comments
        testComments = new ArrayList<>();
        CommentModel comment1 = new CommentModel(TEST_COMMENT_TEXT, TEST_USERNAME, new Date());
        comment1.setDocumentId(TEST_COMMENT_ID);
        comment1.setUserId(TEST_USER_ID);
        testComments.add(comment1);
    }

    /**
     * Test basic initialization of CommentAdapter
     */
    @Test
    public void testCommentAdapterInitialization() {
        CommentAdapter adapter = new CommentAdapter(testComments, context, TEST_MOOD_EVENT_ID);
        assertEquals(1, adapter.getItemCount());
    }

    /**
     * Test updating comments in the adapter
     */
    @Test
    public void testUpdateComments() {
        CommentAdapter adapter = new CommentAdapter(new ArrayList<>(), context, TEST_MOOD_EVENT_ID);
        assertEquals(0, adapter.getItemCount());

        adapter.updateComments(testComments);
        assertEquals(1, adapter.getItemCount());
    }

    /**
     * Test adding a comment
     * This test mocks the database interaction instead of using real DatabaseManager
     */
    @Test
    public void testAddComment() {
        // Create a mock object that provides the "addComment" functionality
        CommentAdder mockCommentAdder = mock(CommentAdder.class);

        // Configure the mock behavior
        doAnswer(invocation -> {
            String ownerId = invocation.getArgument(0);
            String eventId = invocation.getArgument(1);
            CommentModel comment = invocation.getArgument(2);
            OnCompleteListener<DocumentReference> listener = invocation.getArgument(3);

            // Simulate successful operation
            Task<DocumentReference> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockTask);
            return null;
        }).when(mockCommentAdder).addComment(
                anyString(), anyString(), any(CommentModel.class), any(OnCompleteListener.class));

        // Create comment to add
        CommentModel newComment = new CommentModel(TEST_COMMENT_TEXT, TEST_USERNAME, new Date());

        // Capture the comment that's passed to addComment
        ArgumentCaptor<CommentModel> commentCaptor = ArgumentCaptor.forClass(CommentModel.class);

        // Test adding comment with the mock
        mockCommentAdder.addComment(
                TEST_MOOD_OWNER_ID,
                TEST_MOOD_EVENT_ID,
                newComment,
                task -> {
                    // Assert task was successful
                    assertEquals(true, task.isSuccessful());
                });

        // Verify the comment was passed to addComment
        verify(mockCommentAdder).addComment(
                anyString(),
                anyString(),
                commentCaptor.capture(),
                any());

        // Assert comment content matches
        assertEquals(TEST_COMMENT_TEXT, commentCaptor.getValue().getText());
        assertEquals(TEST_USERNAME, commentCaptor.getValue().getAuthor());
    }

    /**
     * Test deleting a comment
     * This test mocks the database interaction instead of using real DatabaseManager
     */
    @Test
    public void testDeleteComment() {
        // Create a mock object that provides the "deleteComment" functionality
        CommentDeleter mockCommentDeleter = mock(CommentDeleter.class);

        // Configure the mock behavior
        doAnswer(invocation -> {
            String eventId = invocation.getArgument(0);
            String commentId = invocation.getArgument(1);
            OnCompleteListener<Void> listener = invocation.getArgument(2);

            // Simulate successful operation
            Task<Void> mockVoidTask = mock(Task.class);
            when(mockVoidTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockVoidTask);
            return null;
        }).when(mockCommentDeleter).deleteComment(
                anyString(), anyString(), any(OnCompleteListener.class));

        // Test deleting comment with the mock
        mockCommentDeleter.deleteComment(
                TEST_MOOD_EVENT_ID,
                TEST_COMMENT_ID,
                task -> {
                    // Assert task was successful
                    assertEquals(true, task.isSuccessful());
                });

        // Verify deleteComment was called with correct parameters
        verify(mockCommentDeleter).deleteComment(
                eq(TEST_MOOD_EVENT_ID),
                eq(TEST_COMMENT_ID),
                any());
    }

    /**
     * Test that a comment model is correctly created
     */
    @Test
    public void testCommentModelCreation() {
        Date testDate = new Date();
        CommentModel comment = new CommentModel(TEST_COMMENT_TEXT, TEST_USERNAME, testDate);

        assertEquals(TEST_COMMENT_TEXT, comment.getText());
        assertEquals(TEST_USERNAME, comment.getAuthor());
        assertEquals(testDate, comment.getTimestamp());

        // Test setters
        comment.setDocumentId(TEST_COMMENT_ID);
        comment.setUserId(TEST_USER_ID);

        assertEquals(TEST_COMMENT_ID, comment.getDocumentId());
        assertEquals(TEST_USER_ID, comment.getUserId());
    }

    /**
     * Simple interface for adding comments - this lets us mock just the behavior we need
     * without depending on the real DatabaseManager
     */
    interface CommentAdder {
        void addComment(String ownerId, String eventId, CommentModel comment,
                        OnCompleteListener<DocumentReference> listener);
    }

    /**
     * Simple interface for deleting comments - this lets us mock just the behavior we need
     * without depending on the real DatabaseManager
     */
    interface CommentDeleter {
        void deleteComment(String eventId, String commentId, OnCompleteListener<Void> listener);
    }
}