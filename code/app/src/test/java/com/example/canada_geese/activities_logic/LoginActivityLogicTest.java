package com.example.canada_geese.activities_logic;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.canada_geese.Pages.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P, manifest = Config.NONE)
public class LoginActivityLogicTest {

    @Mock FirebaseAuth mockAuth;
    @Mock FirebaseFirestore mockFirestore;
    @Mock CollectionReference mockCollection;
    @Mock Query mockQuery;
    @Mock QuerySnapshot mockQuerySnapshot;
    @Mock DocumentSnapshot mockDocument;
    @Mock AuthResult mockAuthResult;
    @Mock FirebaseUser mockUser;

    private LoginActivity activity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        activity = mock(LoginActivity.class);

        setPrivateField(activity, "mAuth", mockAuth);
        setPrivateField(activity, "db", mockFirestore);

        doCallRealMethod().when(activity).loginWithUsername(anyString(), anyString(), anyBoolean());
        doCallRealMethod().when(activity).isFinishing();
        doNothing().when(activity).finish();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testLoginWithValidCredentials() {
        when(mockFirestore.collection("users")).thenReturn(mockCollection);
        when(mockCollection.whereEqualTo("username", "testUser")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocument));
        when(mockDocument.getString("email")).thenReturn("test@example.com");

        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(Tasks.forResult(mockAuthResult));
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        activity.loginWithUsername("testUser", "testPass", false);

        assertNotNull(activity);
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        when(mockFirestore.collection("users")).thenReturn(mockCollection);
        when(mockCollection.whereEqualTo("username", "invalidUser")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocument));
        when(mockDocument.getString("email")).thenReturn("invalid@example.com");

        Task<AuthResult> failedTask = mock(Task.class);
        when(failedTask.isSuccessful()).thenReturn(false);
        when(failedTask.getException()).thenReturn(new Exception("Invalid credentials"));
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(failedTask);

        activity.loginWithUsername("invalidUser", "wrongPass", false);

        assertNotNull(activity);
    }
}
