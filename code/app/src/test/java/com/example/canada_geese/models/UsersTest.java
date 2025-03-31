package com.example.canada_geese.models;

import static org.junit.Assert.*;

import com.example.canada_geese.Models.Users;

import org.junit.Before;
import org.junit.Test;

public class UsersTest {

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_IMAGE_PROFILE = "profile.jpg";
    private static final String TEST_ABOUT = "Hello world";
    private static final String TEST_USER_ID = "user123";

    private Users user;

    @Before
    public void setUp() {
        user = new Users(TEST_USERNAME, TEST_IMAGE_PROFILE);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_IMAGE_PROFILE, user.getImage_profile());

        assertNull(user.getAbout());
        assertNull(user.getUserId());

        Users emptyUser = new Users();
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getImage_profile());
    }

    @Test
    public void testSetAndGetAbout() {

        assertNull(user.getAbout());

        user.setAbout(TEST_ABOUT);
        assertEquals(TEST_ABOUT, user.getAbout());

        user.setAbout("");
        assertEquals("", user.getAbout());

        user.setAbout(null);
        assertNull(user.getAbout());
    }

    @Test
    public void testSetAndGetUserId() {

        assertNull(user.getUserId());

        user.setUserId(TEST_USER_ID);
        assertEquals(TEST_USER_ID, user.getUserId());

        user.setUserId("");
        assertEquals("", user.getUserId());

        user.setUserId(null);
        assertNull(user.getUserId());
    }

    @Test
    public void testAllGetters() {
        user.setAbout(TEST_ABOUT);
        user.setUserId(TEST_USER_ID);

        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_IMAGE_PROFILE, user.getImage_profile());
        assertEquals(TEST_ABOUT, user.getAbout());
        assertEquals(TEST_USER_ID, user.getUserId());
    }

    @Test
    public void testSetAndGetImageProfile() {
        assertEquals("Initial image profile should match constructor", TEST_IMAGE_PROFILE, user.getImage_profile());

        String newImageProfile = "new_profile.jpg";

        assertEquals("Image profile should remain unchanged", TEST_IMAGE_PROFILE, user.getImage_profile());
    }

}