package com.example.canada_geese;

import static org.junit.Assert.*;

import com.example.canada_geese.Models.FollowRequestModel;

import org.junit.Before;
import org.junit.Test;

public class FollowRequestModelTest {

    private static final String TEST_USERNAME = "testUser123";
    private static final String TEST_STATUS_PENDING = "pending";
    private static final String TEST_STATUS_ACCEPTED = "accepted";

    private FollowRequestModel followRequest;

    @Before
    public void setUp() {
        followRequest = new FollowRequestModel(TEST_USERNAME, TEST_STATUS_PENDING);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(TEST_USERNAME, followRequest.getUsername());
        assertEquals(TEST_STATUS_PENDING, followRequest.getStatus());

        FollowRequestModel emptyRequest = new FollowRequestModel();
        assertNull(emptyRequest.getUsername());
        assertNull(emptyRequest.getStatus());
    }

    @Test
    public void testSetStatus() {
        followRequest.setStatus(TEST_STATUS_ACCEPTED);
        assertEquals(TEST_STATUS_ACCEPTED, followRequest.getStatus());

        followRequest.setStatus(null);
        assertNull(followRequest.getStatus());

        followRequest.setStatus("");
        assertEquals("", followRequest.getStatus());
    }

    @Test
    public void testUsernameBehavior() {
        assertEquals(TEST_USERNAME, followRequest.getUsername());
    }
}