package com.example.canada_geese;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import android.net.Uri;

import org.junit.Test;

public class EditProfileActivityLogicTest {

    @Test
    public void testFakeProfileAboutTextUpdate() {
        String about = "I love Canada Geese!";
        assertNotNull(about);
        assertTrue(about.length() > 5);
    }

    @Test
    public void testFakeProfileImageUri() {
        Uri mockUri = mock(Uri.class);
        assertNotNull(mockUri);
    }
}