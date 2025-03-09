package com.example.canada_geese;


import org.junit.Test;
import static org.junit.Assert.*;


import com.example.canada_geese.Models.ExampleModel;


public class ExampleModelTest {


    @Test
    public void testExampleModelInstantiation() {
        ExampleModel exampleModel = new ExampleModel();
        assertNotNull("ExampleModel should be instantiable", exampleModel);
    }


    @Test
    public void testMultipleInstancesAreIndependent() {
        ExampleModel model1 = new ExampleModel();
        ExampleModel model2 = new ExampleModel();
        assertNotSame("Each instance should be independent", model1, model2);
    }


    @Test
    public void testExampleModelDefaultState() {
        try {
            ExampleModel model = new ExampleModel();
            assertNotNull("ExampleModel should be instantiable", model);
        } catch (Exception e) {
            fail("ExampleModel instantiation should not throw an exception");
        }
    }
}

