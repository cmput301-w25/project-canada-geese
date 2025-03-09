package com.example.canada_geese.ModelTest;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.canada_geese.Models.ExampleModel;

public class ExampleModelTest {

    @Test
    public void testExampleModelInstantiation() {
        // 创建 ExampleModel 的实例
        ExampleModel exampleModel = new ExampleModel();
        assertNotNull("ExampleModel should be instantiable", exampleModel);
    }

    @Test
    public void testMultipleInstancesAreIndependent() {
        // 测试多个实例是否为独立对象
        ExampleModel model1 = new ExampleModel();
        ExampleModel model2 = new ExampleModel();
        assertNotSame("Each instance should be independent", model1, model2);
    }

    @Test
    public void testExampleModelDefaultState() {
        // 由于 ExampleModel 当前为空，我们可以验证默认状态下无异常
        try {
            ExampleModel model = new ExampleModel();
            assertNotNull("ExampleModel should be instantiable", model);
        } catch (Exception e) {
            fail("ExampleModel instantiation should not throw an exception");
        }
    }

    // 提醒：如未来 ExampleModel 添加功能，请在此扩展测试
}
