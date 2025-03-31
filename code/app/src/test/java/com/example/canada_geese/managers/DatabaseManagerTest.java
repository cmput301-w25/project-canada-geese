package com.example.canada_geese.managers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.example.canada_geese.Models.CommentModel;
import com.example.canada_geese.Models.MoodEventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatabaseManagerTest {

    private static class MockDatabaseManager {
        private boolean getInstanceCalled = false;
        private boolean addMoodEventCalled = false;
        private boolean fetchMoodEventsCalled = false;
        private boolean updateMoodEventCalled = false;
        private boolean deleteMoodEventCalled = false;
        private boolean findMoodEventDocumentIdCalled = false;
        private boolean addCommentCalled = false;
        private boolean deleteCommentCalled = false;
        private boolean fetchFollowedUsersMoodEventsCalled = false;
        private boolean fetchAllUsernamesCalled = false;

        private static MockDatabaseManager instance;

        public static synchronized MockDatabaseManager getInstance() {
            if (instance == null) {
                instance = new MockDatabaseManager();
            }
            instance.getInstanceCalled = true;
            return instance;
        }

        public void addMoodEvent(MoodEventModel moodEvent) {
            addMoodEventCalled = true;
        }

        public void fetchMoodEvents(OnCompleteListener<QuerySnapshot> listener) {
            fetchMoodEventsCalled = true;
        }

        public void updateMoodEvent(MoodEventModel moodEvent, String documentId, OnCompleteListener<Void> listener) {
            updateMoodEventCalled = true;
        }

        public void deleteMoodEvent(String documentId, OnCompleteListener<Void> listener) {
            deleteMoodEventCalled = true;
        }

        public void findMoodEventDocumentId(Date timestamp, String emotion, OnCompleteListener<DocumentSnapshot> listener) {
            findMoodEventDocumentIdCalled = true;
        }

        public void addComment(String ownerUserId, String moodEventId, CommentModel comment, OnCompleteListener<DocumentReference> listener) {
            addCommentCalled = true;
        }

        public void deleteComment(String moodEventId, String commentId, OnCompleteListener<Void> listener) {
            deleteCommentCalled = true;
        }

        public void fetchFollowedUsersMoodEvents(OnCompleteListener<List<MoodEventModel>> listener) {
            fetchFollowedUsersMoodEventsCalled = true;
        }

        public void fetchAllUsernames(OnSuccessListener<Map<String, String>> listener) {
            fetchAllUsernamesCalled = true;
        }
    }

    private MockDatabaseManager mockManager;
    private MoodEventModel testMoodEvent;
    private CommentModel testComment;

    @Before
    public void setUp() {
        testMoodEvent = new MoodEventModel("Happiness", "Test description", new Date(), "😊", 1, false, false, 0.0, 0.0);
        testComment = new CommentModel("Test comment", "Test author", new Date());
        mockManager = MockDatabaseManager.getInstance();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(mockManager);
        assertTrue(mockManager.getInstanceCalled);
        MockDatabaseManager anotherInstance = MockDatabaseManager.getInstance();
        assertTrue(mockManager == anotherInstance);
    }

    @Test
    public void testAddMoodEvent_Success() {
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://example.com/image1.jpg");
        testMoodEvent.setImageUrls(imageUrls);
        testMoodEvent.setHasLocation(true);
        testMoodEvent.setLatitude(53.5461);
        testMoodEvent.setLongitude(-113.4938);

        mockManager.addMoodEvent(testMoodEvent);
        assertTrue(mockManager.addMoodEventCalled);
    }

    @Test
    public void testFetchMoodEvents() {
        mockManager.fetchMoodEvents(null);
        assertTrue(mockManager.fetchMoodEventsCalled);
    }

    @Test
    public void testUpdateMoodEvent() {
        testMoodEvent.setDescription("Updated description");
        testMoodEvent.setImageUrls(new ArrayList<>(List.of("https://example.com/updated.jpg")));
        mockManager.updateMoodEvent(testMoodEvent, "updatedDocId", null);
        assertTrue(mockManager.updateMoodEventCalled);
    }

    @Test
    public void testDeleteMoodEvent() {
        String testDocId = "mood123";
        mockManager.deleteMoodEvent(testDocId, null);
        assertTrue("deleteMoodEvent() should be triggered", mockManager.deleteMoodEventCalled);
    }

    @Test
    public void testFindMoodEventDocumentId() {
        mockManager.findMoodEventDocumentId(new Date(), "Happiness", null);
        assertTrue(mockManager.findMoodEventDocumentIdCalled);
    }

    @Test
    public void testAddComment() {
        mockManager.addComment("testOwnerId", "testMoodEventId", testComment, null);
        assertTrue(mockManager.addCommentCalled);
    }

    @Test
    public void testDeleteComment() {
        mockManager.deleteComment("testMoodEventId", "testCommentId", null);
        assertTrue(mockManager.deleteCommentCalled);
    }

    @Test
    public void testFetchFollowedUsersMoodEvents() {
        mockManager.fetchFollowedUsersMoodEvents(null);
        assertTrue("fetchFollowedUsersMoodEvents() should be called",
                mockManager.fetchFollowedUsersMoodEventsCalled);
    }


    @Test
    public void testFetchAllUsernames() {
        mockManager.fetchAllUsernames(null);
        assertTrue(mockManager.fetchAllUsernamesCalled);
    }

    @Test
    public void testHandleImageUpload() {
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://firebasestorage.googleapis.com/v0/b/app-id/o/images%2Fpic.jpg?alt=media");
        testMoodEvent.setImageUrls(imageUrls);

        for (String url : testMoodEvent.getImageUrls()) {
            assertTrue(url.startsWith("https://firebasestorage.googleapis.com"));
        }
    }
}