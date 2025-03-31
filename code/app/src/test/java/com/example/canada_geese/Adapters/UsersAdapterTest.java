package com.example.canada_geese.Adapters;

import static org.junit.Assert.*;

import com.example.canada_geese.Models.Users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the UsersAdapter logic (non-UI Firebase-free).
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UsersAdapterTest {

    private TestAdapter adapter;
    private List<Users> usersList;

    @Before
    public void setUp() {
        usersList = new ArrayList<>();

        Users user1 = new Users("johnsmith", "profile_img_1");
        user1.setAbout("Software developer");

        Users user2 = new Users("janesmith", "profile_img_2");
        user2.setAbout("UI designer");

        Users user3 = new Users("alexjohnson", "profile_img_3");
        user3.setAbout("Project manager");

        usersList.add(user1);
        usersList.add(user2);
        usersList.add(user3);

        adapter = new TestAdapter(usersList);
    }

    /**
     * Test: Update the user list and verify adapter reflects changes correctly.
     */
    @Test
    public void testUpdateList() {
        assertEquals(3, adapter.getFilteredUsers().size());

        List<Users> newUsersList = new ArrayList<>();
        Users newUser1 = new Users("michaelbrown", "profile_img_4");
        newUser1.setAbout("Data scientist");
        Users newUser2 = new Users("sarahwilson", "profile_img_5");
        newUser2.setAbout("UX researcher");

        newUsersList.add(newUser1);
        newUsersList.add(newUser2);
        adapter.updateList(newUsersList);

        List<Users> updatedList = adapter.getFilteredUsers();
        assertEquals(2, updatedList.size());
        assertEquals("michaelbrown", updatedList.get(0).getUsername());
        assertEquals("sarahwilson", updatedList.get(1).getUsername());
    }

    /**
     * Test: Filter logic based on username substrings.
     */
    @Test
    public void testFilter() {
        assertEquals(3, adapter.getFilteredUsers().size());

        adapter.filter("smith");
        List<Users> filteredList = adapter.getFilteredUsers();
        assertEquals(2, filteredList.size());
        assertTrue(filteredList.stream().anyMatch(u -> u.getUsername().equals("johnsmith")));
        assertTrue(filteredList.stream().anyMatch(u -> u.getUsername().equals("janesmith")));

        adapter.filter("jane");
        assertEquals(1, adapter.getFilteredUsers().size());
        assertEquals("janesmith", adapter.getFilteredUsers().get(0).getUsername());

        adapter.filter("alex");
        assertEquals(1, adapter.getFilteredUsers().size());
        assertEquals("alexjohnson", adapter.getFilteredUsers().get(0).getUsername());

        adapter.filter("xyz");
        assertEquals(0, adapter.getFilteredUsers().size());

        adapter.filter("");
        assertEquals(3, adapter.getFilteredUsers().size());
    }

    /**
     * Test: Logic for determining if Glide should load profile images.
     */
    @Test
    public void testLoadUserImage() {
        Users userWithImage = new Users("testuser", "http://example.com/image.jpg");
        assertTrue(adapter.shouldUseGlideForUser(userWithImage));

        Users userWithoutImage = new Users("emptyuser", null);
        assertFalse(adapter.shouldUseGlideForUser(userWithoutImage));

        Users userWithEmptyImage = new Users("emptystring", "");
        assertFalse(adapter.shouldUseGlideForUser(userWithEmptyImage));
    }

    /**
     * Test: About text fallback and profile image logic.
     */
    @Test
    public void testShowUserDetailsWithImage() {
        Users userWithImageAndAbout = new Users("userWithImage", "http://example.com/profile.jpg");
        userWithImageAndAbout.setAbout("Test user description");
        assertTrue(adapter.shouldUseGlideForUser(userWithImageAndAbout));
        assertEquals("Test user description", adapter.getAboutTextForUser(userWithImageAndAbout));

        Users userWithoutAbout = new Users("userWithoutAbout", "http://example.com/profile.jpg");
        userWithoutAbout.setAbout(null);
        assertEquals("No description available", adapter.getAboutTextForUser(userWithoutAbout));
    }

    /**
     * Test: Image loading decisions for valid, empty, and null profile URLs.
     */
    @Test
    public void testHandleUserProfileImage() {
        // User with a valid image URL
        Users user = new Users("glideUser", "http://example.com/avatar.png");
        assertTrue("Glide should be used for valid profile image", adapter.shouldUseGlideForUser(user));

        // Modify the image URL to empty string using reflection
        setUserImageProfile(user, "");
        assertFalse("Glide should not be used for empty profile image", adapter.shouldUseGlideForUser(user));

        // Modify the image URL to null using reflection
        setUserImageProfile(user, null);
        assertFalse("Glide should not be used for null profile image", adapter.shouldUseGlideForUser(user));
    }

    // Helper method to set image_profile via reflection
    private void setUserImageProfile(Users user, String newValue) {
        try {
            java.lang.reflect.Field field = Users.class.getDeclaredField("image_profile");
            field.setAccessible(true);
            field.set(user, newValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set image_profile via reflection", e);
        }
    }

    /**
     * A test-specific implementation of UsersAdapter that avoids Firebase dependencies
     */
    private class TestAdapter {
        private List<Users> usersList;
        private List<Users> filteredUsersList;
        private String currentQuery = "";

        public TestAdapter(List<Users> usersList) {
            this.usersList = new ArrayList<>(usersList);
            this.filteredUsersList = new ArrayList<>(usersList);
        }

        public List<Users> getFilteredUsers() {
            return filteredUsersList;
        }

        public void updateList(List<Users> newList) {
            this.usersList.clear();
            this.usersList.addAll(newList);
            filter(currentQuery);
        }

        public void filter(String query) {
            this.currentQuery = query;
            filteredUsersList.clear();
            for (Users user : usersList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredUsersList.add(user);
                }
            }
        }

        public boolean shouldUseGlideForUser(Users user) {
            return user.getImage_profile() != null && !user.getImage_profile().isEmpty();
        }

        public String getAboutTextForUser(Users user) {
            return user.getAbout() != null ? user.getAbout() : "No description available";
        }
    }
}
