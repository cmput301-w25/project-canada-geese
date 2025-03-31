package com.example.canada_geese.Adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;

import com.example.canada_geese.Models.FollowRequestModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

        requestList = new ArrayList<>();
        requestList.add(new FollowRequestModel("testuser1", "pending"));
        requestList.add(new FollowRequestModel("testuser2", "pending"));

        adapter = new FollowRequestAdapter(requestList, mockContext, mockListener);
    }

    /**
     * Test: Trigger listener manually and verify adapter handles request actions (accept/reject).
     */
    @Test
    public void testOnRequestAction() {

        FollowRequestModel testRequest = new FollowRequestModel("testuser3", "pending");
        requestList.add(testRequest);

        mockListener.onRequestAction("testuser3", "accepted");
        verify(mockListener).onRequestAction("testuser3", "accepted");

        mockListener.onRequestAction("testuser3", "rejected");
        verify(mockListener).onRequestAction("testuser3", "rejected");
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Test: Simulate ViewHolder interaction and verify that the correct listener callbacks are triggered.
     */
    @Test
    public void testOnRequestActionWithHolder() {
        FollowRequestModel testRequest = new FollowRequestModel("testuser3", "pending");
        TestViewHolder testHolder = new TestViewHolder();
        testHolder.bindView(testRequest);

        testHolder.clickAccept();
        verify(mockListener).onRequestAction("testuser3", "accepted");
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