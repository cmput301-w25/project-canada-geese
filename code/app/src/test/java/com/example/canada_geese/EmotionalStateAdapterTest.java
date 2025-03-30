package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;

import com.example.canada_geese.Adapters.EmotionalStateAdapter;
import com.example.canada_geese.Models.EmotionalState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EmotionalStateAdapterTest {

    @Mock
    private Context mockContext;

    @Mock
    private EmotionalStateAdapter.OnEmotionalStateSelectedListener mockListener;

    private EmotionalStateAdapter adapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adapter = new EmotionalStateAdapter(mockListener);
    }

    @Test
    public void testSetSelectedState() {
        // Initially selected state should be null
        EmotionalState initialSelectedState = getSelectedState(adapter);
        assertEquals(null, initialSelectedState);

        // Set selected state to HAPPINESS
        adapter.setSelectedState(EmotionalState.HAPPINESS);

        // Verify selected state was updated
        EmotionalState updatedSelectedState = getSelectedState(adapter);
        assertEquals(EmotionalState.HAPPINESS, updatedSelectedState);

        // Change to another state
        adapter.setSelectedState(EmotionalState.SADNESS);
        updatedSelectedState = getSelectedState(adapter);
        assertEquals(EmotionalState.SADNESS, updatedSelectedState);

        // Set to null
        adapter.setSelectedState(null);
        updatedSelectedState = getSelectedState(adapter);
        assertEquals(null, updatedSelectedState);
    }

    // Helper method to access the selected state using reflection
    private EmotionalState getSelectedState(EmotionalStateAdapter adapter) {
        try {
            Field field = EmotionalStateAdapter.class.getDeclaredField("selectedState");
            field.setAccessible(true);
            return (EmotionalState) field.get(adapter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access selectedState field", e);
        }
    }
}