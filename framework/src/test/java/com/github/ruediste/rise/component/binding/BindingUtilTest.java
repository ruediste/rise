package com.github.ruediste.rise.component.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaRunner;

@RunWith(LambdaRunner.class)
public class BindingUtilTest {

    private static class TestModel extends ControllerComponent {
        public String str;

        public TestModel model;

        public String getStrReadonly() {
            return "foo";
        }
    }

    private TestModel model;

    <T> T capture(@Capture T value) {
        return value;
    }

    @Before
    public void before() {
        model = new TestModel();
    }

    @Test
    public void simple() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.str));
        assertEquals("str", info.modelProperty.getName());
        assertTrue(info.isTwoWay);
    }

    @Test
    public void oneWay() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.getStrReadonly()));
        assertFalse(info.isTwoWay);
    }

    @Test
    public void simpleBinding() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.str));

        model.str = "bar";

        info.setModelProperty("Hello");
        assertEquals("Hello", model.str);
    }

    TwoWayBindingTransformer<String, Integer> stringToIntTransformer = new TwoWayBindingTransformer<String, Integer>() {

        @Override
        protected Integer transformImpl(String source) {
            return Integer.valueOf(source);
        }

        @Override
        protected String transformInvImpl(Integer target) {
            return Objects.toString(target);
        }
    };

    BindingTransformer<String, Integer> stringToIntTransformerOneWay = new BindingTransformer<String, Integer>() {

        @Override
        protected Integer transformImpl(String source) {
            return Integer.valueOf(source);
        }
    };

    @Test
    public void withTransformer() {
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformer.transform(model.str)));
        assertEquals("str", info.modelProperty.getName());
        assertEquals(stringToIntTransformer, info.transformer);
        assertTrue(info.isTwoWay);
    }

    @Test
    public void withTransformerBinding() {
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformer.transform(model.str)));

        info.setModelProperty(5);
        assertEquals("5", model.str);
    }

    @Test
    public void withTransformerOneWay() {
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformerOneWay.transform(model.str)));
        assertFalse(info.isTwoWay);
    }

    @Test
    public void modelPropertyPath() {
        BindingInfo<?> info = BindingUtil.extractBindingInfo(capture(() -> model.model.str));
        assertEquals("model.model.str", info.modelPropertyPath.get());
        info = BindingUtil.extractBindingInfo(capture(() -> model.getStrReadonly()));
        assertEquals("model.strReadonly", info.modelPropertyPath.get());
    }
}
