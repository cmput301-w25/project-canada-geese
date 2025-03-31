package com.example.canada_geese.Logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnFailureListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EditProfileLogicTest {

    @Mock
    private Context mockContext;

    @Mock
    private ContentResolver mockContentResolver;

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private CollectionReference mockCollectionRef;

    @Mock
    private FirebaseStorage mockStorage;

    @Mock
    private StorageReference mockStorageRef;

    @Mock
    private StorageReference mockFileRef;

    @Mock
    private UploadTask mockUploadTask;

    @Mock
    private Task<Void> mockTask;

    @Mock
    private Task<Uri> mockUriTask;

    @Mock
    private Uri mockUri;

    @Mock
    private Bitmap mockBitmap;

    private ProfileEditor profileEditor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test_user_id");
        when(mockDb.collection(anyString())).thenReturn(mockCollectionRef);
        when(mockCollectionRef.document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.update(any(Map.class))).thenReturn(mockTask);
        when(mockStorage.getReference(anyString())).thenReturn(mockStorageRef);
        when(mockStorageRef.child(anyString())).thenReturn(mockFileRef);
        when(mockFileRef.putFile(any(Uri.class))).thenReturn(mockUploadTask);
        when(mockUploadTask.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(mockUploadTask);
        when(mockUploadTask.addOnFailureListener(any(OnFailureListener.class))).thenReturn(mockUploadTask);
        when(mockUri.toString()).thenReturn("content://test/image.jpg");
        profileEditor = new ProfileEditor(mockContext, mockAuth, mockDb, mockStorage);
    }

    /**
     * Test: Save a profile with an image URI.
     * Verifies that both about text and uploaded image URL are passed into Firestore update.
     */
    @Test
    public void testSaveProfileWithImage() {
        String aboutText = "Test user bio";
        Uri imageUri = mockUri;
        final String[] uploadedImageUrl = {null};

        doAnswer(new Answer<UploadTask>() {
            @Override
            public UploadTask answer(InvocationOnMock invocation) throws Throwable {
                OnSuccessListener<UploadTask.TaskSnapshot> listener = invocation.getArgument(0);
                listener.onSuccess(mock(UploadTask.TaskSnapshot.class));
                return mockUploadTask;
            }
        }).when(mockUploadTask).addOnSuccessListener(any(OnSuccessListener.class));

        when(mockFileRef.getDownloadUrl()).thenReturn(mockUriTask);
        doAnswer(new Answer<Task<Uri>>() {
            @Override
            public Task<Uri> answer(InvocationOnMock invocation) throws Throwable {
                OnSuccessListener<Uri> listener = invocation.getArgument(0);
                Uri downloadUri = Uri.parse("https://firebasestorage.example.com/test_image.jpg");
                uploadedImageUrl[0] = downloadUri.toString();
                listener.onSuccess(downloadUri);
                return mockUriTask;
            }
        }).when(mockUriTask).addOnSuccessListener(any(OnSuccessListener.class));

        final Map<String, Object>[] capturedUpdates = new Map[1];
        doAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) throws Throwable {
                capturedUpdates[0] = invocation.getArgument(0);
                return mockTask;
            }
        }).when(mockDocRef).update(any(Map.class));
        boolean result = profileEditor.saveProfile(aboutText, imageUri);

        // Verify the result
        assertTrue("Profile save operation should return true", result);
        assertNotNull("Should have captured updates", capturedUpdates[0]);
        assertEquals("About text should match", aboutText, capturedUpdates[0].get("about"));
        assertEquals("Image URL should match", "https://firebasestorage.example.com/test_image.jpg",
                capturedUpdates[0].get("image_profile"));
    }

    /**
     * Test: Remove the profile picture.
     * Ensures that the Firestore update sets 'image_profile' field to null.
     */
    @Test
    public void testRemoveProfilePicture() {
        final Map<String, Object>[] capturedUpdates = new Map[1];
        doAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) throws Throwable {
                capturedUpdates[0] = invocation.getArgument(0);
                return mockTask;
            }
        }).when(mockDocRef).update(any(Map.class));

        boolean result = profileEditor.removeProfilePicture();

        assertTrue("Profile picture removal should return true", result);
        assertNotNull("Should have captured updates", capturedUpdates[0]);
        assertNull("Image profile should be set to null", capturedUpdates[0].get("image_profile"));
        verify(mockDocRef).update(eq(capturedUpdates[0]));
    }

    @Test
    public void testGetImageUri() {
        ProfileEditor testEditor = new ProfileEditor(mockContext, mockAuth, mockDb, mockStorage) {
            @Override
            protected String insertImageToMediaStore(ContentResolver resolver, Bitmap bitmap, String title, String description) {
                return "content://media/external/images/1234";
            }
        };

        Uri result = testEditor.getImageUri(mockBitmap);
        assertNotNull(result);
        assertEquals("content://media/external/images/1234", result.toString());
    }


    /**
     * Helper class that encapsulates the profile editing logic we want to test
     */
    private class ProfileEditor {
        private Context context;
        private FirebaseAuth auth;
        private FirebaseFirestore db;
        private FirebaseStorage storage;

        public ProfileEditor(Context context, FirebaseAuth auth, FirebaseFirestore db, FirebaseStorage storage) {
            this.context = context;
            this.auth = auth;
            this.db = db;
            this.storage = storage;
        }

        public boolean saveProfile(String aboutText, Uri imageUri) {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                return false;
            }

            String userId = user.getUid();
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("about", aboutText);

            if (imageUri != null) {
                StorageReference fileReference = storage.getReference("profile_images").child(userId + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                userUpdates.put("image_profile", uri.toString());
                                updateUserProfile(userId, userUpdates);
                            });
                        })
                        .addOnFailureListener(e -> {
                        });
            } else {
                updateUserProfile(userId, userUpdates);
            }

            return true;
        }

        private void updateUserProfile(String userId, Map<String, Object> userUpdates) {
            db.collection("users").document(userId).update(userUpdates);
        }

        public boolean removeProfilePicture() {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                return false;
            }

            String userId = user.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("image_profile", null);

            db.collection("users").document(userId).update(updates);
            return true;
        }

        public Uri getImageUri(Bitmap bitmap) {
            String path = insertImageToMediaStore(context.getContentResolver(), bitmap, "Title", null);
            return Uri.parse(path);
        }


        protected String insertImageToMediaStore(ContentResolver resolver, Bitmap bitmap, String title, String description) {
            return MediaStore.Images.Media.insertImage(resolver, bitmap, title, description);
        }

    }
}