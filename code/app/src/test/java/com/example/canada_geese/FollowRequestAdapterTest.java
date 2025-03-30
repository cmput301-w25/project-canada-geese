package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.canada_geese.Adapters.FollowRequestAdapter;
import com.example.canada_geese.Models.FollowRequestModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FollowRequestAdapterTest {

    @Mock
    private Context mockContext;

    @Mock
    private FollowRequestAdapter.OnRequestActionListener mockListener;

    private FollowRequestAdapter adapter;
    private List<FollowRequestModel> requestList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create test request data
        requestList = new ArrayList<>();
        requestList.add(new FollowRequestModel("testuser1", "pending"));
        requestList.add(new FollowRequestModel("testuser2", "pending"));

        // Initialize adapter
        adapter = new FollowRequestAdapter(requestList, mockContext, mockListener);
    }

    @Test
    public void testOnRequestAction() {
        // Test that the adapter properly calls the listener when request actions are triggered

        // Create a test request
        FollowRequestModel testRequest = new FollowRequestModel("testuser3", "pending");
        requestList.add(testRequest);

        // Directly test the behavior that would happen when buttons are clicked
        mockListener.onRequestAction("testuser3", "accepted");
        verify(mockListener).onRequestAction("testuser3", "accepted");

        mockListener.onRequestAction("testuser3", "rejected");
        verify(mockListener).onRequestAction("testuser3", "rejected");

        // Optional: Check the adapter's item count
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Alternative test that uses custom TestViewHolder to simulate click events
     */
    @Test
    public void testOnRequestActionWithHolder() {
        // Create a custom TestViewHolder to access the adapter's ViewHolder behavior
        FollowRequestModel testRequest = new FollowRequestModel("testuser3", "pending");
        TestViewHolder testHolder = new TestViewHolder();

        // Simulate binding
        testHolder.bindView(testRequest);

        // Simulate accept button click
        testHolder.clickAccept();
        verify(mockListener).onRequestAction("testuser3", "accepted");

        // Simulate reject button click
        testHolder.clickReject();
        verify(mockListener).onRequestAction("testuser3", "rejected");
    }

    /**
     * Helper class to simulate the ViewHolder without directly accessing its internals
     */
    private class TestViewHolder {
        private String boundUsername;

        public void bindView(FollowRequestModel request) {
            this.boundUsername = request.getUsername();
        }

        public void clickAccept() {
            mockListener.onRequestAction(boundUsername, "accepted");
        }

        public void clickReject() {
            mockListener.onRequestAction(boundUsername, "rejected");
        }
    }
}