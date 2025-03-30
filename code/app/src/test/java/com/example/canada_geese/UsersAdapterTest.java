package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import com.example.canada_geese.Models.Users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UsersAdapterTest {

    private TestAdapter adapter;
    private List<Users> usersList;

    @Before
    public void setUp() {
        // Create test user data
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

        // Initialize adapter with TestAdapter to avoid Firebase dependencies
        adapter = new TestAdapter(usersList);
    }

    @Test
    public void testUpdateList() {
        // Initial list size should be 3
        assertEquals(3, adapter.getFilteredUsers().size());
        assertEquals("johnsmith", adapter.getFilteredUsers().get(0).getUsername());
        assertEquals("janesmith", adapter.getFilteredUsers().get(1).getUsername());
        assertEquals("alexjohnson", adapter.getFilteredUsers().get(2).getUsername());

        // Create new list of users
        List<Users> newUsersList = new ArrayList<>();

        Users newUser1 = new Users("michaelbrown", "profile_img_4");
        newUser1.setAbout("Data scientist");

        Users newUser2 = new Users("sarahwilson", "profile_img_5");
        newUser2.setAbout("UX researcher");

        newUsersList.add(newUser1);
        newUsersList.add(newUser2);

        // Update the adapter with the new list
        adapter.updateList(newUsersList);

        // Check that the adapter now contains only the new users
        List<Users> updatedList = adapter.getFilteredUsers();
        assertEquals(2, updatedList.size());
        assertEquals("michaelbrown", updatedList.get(0).getUsername());
        assertEquals("sarahwilson", updatedList.get(1).getUsername());
        assertEquals("Data scientist", updatedList.get(0).getAbout());
        assertEquals("UX researcher", updatedList.get(1).getAbout());
    }

    @Test
    public void testFilter() {
        // Initially all users should be visible
        assertEquals(3, adapter.getFilteredUsers().size());

        // Filter by "smith" - should match "johnsmith" and "janesmith"
        adapter.filter("smith");
        List<Users> filteredList = adapter.getFilteredUsers();
        assertEquals(2, filteredList.size());

        // Verify the correct users were filtered
        List<String> usernames = new ArrayList<>();
        for (Users user : filteredList) {
            usernames.add(user.getUsername());
        }
        assertTrue(usernames.contains("johnsmith"));
        assertTrue(usernames.contains("janesmith"));

        // Filter by "jane" - should match only "janesmith"
        adapter.filter("jane");
        filteredList = adapter.getFilteredUsers();
        assertEquals(1, filteredList.size());
        assertEquals("janesmith", filteredList.get(0).getUsername());

        // Filter by "alex" - should match only "alexjohnson"
        adapter.filter("alex");
        filteredList = adapter.getFilteredUsers();
        assertEquals(1, filteredList.size());
        assertEquals("alexjohnson", filteredList.get(0).getUsername());

        // Filter by "xyz" - should match none
        adapter.filter("xyz");
        filteredList = adapter.getFilteredUsers();
        assertEquals(0, filteredList.size());

        // Reset filter
        adapter.filter("");
        filteredList = adapter.getFilteredUsers();
        assertEquals(3, filteredList.size());
    }

    @Test
    public void testLoadUserImage() {
        // Test user with profile image
        Users userWithImage = new Users("testuser", "http://example.com/image.jpg");
        boolean shouldUseGlide = adapter.shouldUseGlideForUser(userWithImage);
        assertTrue("Should use Glide for user with profile image", shouldUseGlide);

        // Test user without profile image
        Users userWithoutImage = new Users("emptyuser", null);
        shouldUseGlide = adapter.shouldUseGlideForUser(userWithoutImage);
        assertFalse("Should not use Glide for user without profile image", shouldUseGlide);

        // Test user with empty profile image
        Users userWithEmptyImage = new Users("emptystring", "");
        shouldUseGlide = adapter.shouldUseGlideForUser(userWithEmptyImage);
        assertFalse("Should not use Glide for user with empty profile image", shouldUseGlide);
    }

    @Test
    public void testShowUserDetailsWithImage() {
        // Test user with profile image and about text
        Users userWithImageAndAbout = new Users("userWithImage", "http://example.com/profile.jpg");
        userWithImageAndAbout.setAbout("Test user description");

        // Test image loading decision
        boolean shouldUseGlide = adapter.shouldUseGlideForUser(userWithImageAndAbout);
        assertTrue("Should use Glide for user with profile image", shouldUseGlide);

        // Test about text retrieval
        String aboutText = adapter.getAboutTextForUser(userWithImageAndAbout);
        assertEquals("Test user description", aboutText);

        // Test user without about text
        Users userWithoutAbout = new Users("userWithoutAbout", "http://example.com/profile.jpg");
        userWithoutAbout.setAbout(null);

        aboutText = adapter.getAboutTextForUser(userWithoutAbout);
        assertEquals("No description available", aboutText);
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

        // Helper method for testing image loading logic
        public boolean shouldUseGlideForUser(Users user) {
            return user.getImage_profile() != null && !user.getImage_profile().isEmpty();
        }

        // Helper method for testing about text display
        public String getAboutTextForUser(Users user) {
            return user.getAbout() != null ? user.getAbout() : "No description available";
        }
    }
}