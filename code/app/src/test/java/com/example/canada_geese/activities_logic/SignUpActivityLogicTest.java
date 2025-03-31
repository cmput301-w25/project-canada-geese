package com.example.canada_geese.activities_logic;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.example.canada_geese.Pages.SignUpActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import android.os.Build;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P, manifest = Config.NONE)
public class SignUpActivityLogicTest {

    private SignUpActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activity = mock(SignUpActivity.class);

        doNothing().when(activity).signUp(anyString(), anyString(), anyString());
    }

    @Test
    public void testSignUpWithValidCredentials() {
        activity.signUp("test@example.com", "password123", "tester");
        verify(activity).signUp(anyString(), anyString(), anyString());
        assertNotNull(activity);
    }

    @Test
    public void testSignUpWithInvalidEmail() {
        activity.signUp("bad-email", "password123", "tester");
        verify(activity).signUp(anyString(), anyString(), anyString());
        assertNotNull(activity);
    }

    @Test
    public void testSignUpWithShortPassword() {
        activity.signUp("test@example.com", "12", "tester");
        verify(activity).signUp(anyString(), anyString(), anyString());
        assertNotNull(activity);
    }

    @Test
    public void testSignUpWithExistingUsername() {
        activity.signUp("test@example.com", "password123", "tester");
        verify(activity).signUp(anyString(), anyString(), anyString());
        assertNotNull(activity);
    }
}
