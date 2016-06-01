package com.github.ruediste.rise.component.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.fragment.FragmentCanvas;
import com.github.ruediste.rise.component.fragment.ValueHandle;
import com.github.ruediste.rise.component.fragment.ValueHandleImpl;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaRunner;

@RunWith(LambdaRunner.class)
public class BindingUtilTest {

    private static class TestModel extends ControllerComponent {
        public String str;

        public String getStrReadonly() {
            return "foo";
        }
    }

    private TestModel model;
    private TestView view;

    <T> T capture(@Capture T value) {
        return value;
    }

    @Before
    public void before() {
        model = new TestModel();
        view = new TestView();
        view.setController(model);
    }

    @Test
    public void simple() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.str), null);
        assertEquals("str", info.modelProperty.getName());
        assertTrue(info.isTwoWay);
    }

    @Test
    public void oneWay() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.getStrReadonly()), null);
        assertFalse(info.isTwoWay);
    }

    @Test
    public void simpleBinding() {
        ValueHandle<String> value = new ValueHandleImpl<>();

        BindingInfo<String> info = BindingUtil.extractBindingInfo(capture(() -> model.str), value);

        model.str = "bar";
        Binding binding = info.createBinding(value);
        binding.pullUp();
        assertEquals("bar", value.get());

        value.set("Hello");
        binding.pushDown();
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
        TestModel model = new TestModel();
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformer.transform(model.str)), null);
        assertEquals("str", info.modelProperty.getName());
        assertEquals(stringToIntTransformer, info.transformer);
        assertTrue(info.isTwoWay);
    }

    @Test
    public void withTransformerBinding() {
        TestModel model = new TestModel();
        ValueHandleImpl<Integer> value = new ValueHandleImpl<>();
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformer.transform(model.str)), value);

        model.str = "1";
        Binding binding = info.createBinding(value);
        binding.pullUp();
        assertEquals(1, (int) value.get());

        value.set(5);
        binding.pushDown();
        assertEquals("5", model.str);
    }

    @Test
    public void withTransformerOneWay() {
        TestModel model = new TestModel();
        BindingInfo<Integer> info = BindingUtil
                .extractBindingInfo(capture(() -> stringToIntTransformerOneWay.transform(model.str)), null);
        assertFalse(info.isTwoWay);
    }

    private class TestView extends ViewComponentBase<TestModel> {

        void setController(TestModel ctrl) {
            this.controller = ctrl;
        }

        Supplier<String> str() {
            return capture(() -> controller.str);
        }

        @Override
        protected void render(FragmentCanvas<?> html) {

        }
    }

    @Test
    public void testAccessController() {
        BindingInfo<String> info = BindingUtil.extractBindingInfo(view.str(), null);
        assertTrue(info.accessesController);
    }
}
