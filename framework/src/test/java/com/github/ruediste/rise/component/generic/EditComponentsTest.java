package com.github.ruediste.rise.component.generic;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.core.strategy.UseStrategy;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.jsr330.util.LoggerCreationRule;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.google.common.reflect.TypeToken;

public class EditComponentsTest {

    @Inject
    EditComponents components;

    static class SpecialFactory implements EditComponentFactory {

        @Override
        public Optional<EditComponentWrapper<?>> getComponent(TypeToken<?> type, Optional<String> testName,
                Optional<PropertyInfo> info, Optional<Class<? extends Annotation>> qualifier) {
            return Optional.of(new EditComponentWrapper<Object>() {

                @Override
                public Component getComponent() {
                    return null;
                }

                @Override
                public Object getValue() {
                    return "yeah";
                }

                @Override
                public EditComponentWrapper<Object> setValue(Object value) {
                    return null;
                }

                @Override
                public EditComponentWrapper<Object> bindValue(Supplier<Object> accessor) {
                    return null;
                }
            });
        }

    }

    interface TestA {
        int getA();

        String getString();

        @UseStrategy(SpecialFactory.class)
        String getStringSpecial();
    }

    class TestController {
        @Inject
        BindingGroup<TestA> data;

        TestA data() {
            return data.proxy();
        }
    }

    @Before
    public void before() {
        Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                bindCreationRule(CoreRestartableModule.createMessagesRule());
                binder().bindCreationRule(new LoggerCreationRule(Logger.class, LoggerFactory::getLogger));
                bind(ApplicationStage.class).toInstance(ApplicationStage.DEVELOPMENT);
                bind(ClassLoader.class).toInstance(getClass().getClassLoader());
            }

            @Provides
            TranslatedStringResolver tsr() {
                return null;
            }

            @Provides
            PatternStringResolver resolver() {
                return null;
            }
        }).injectMembers(this);

    }

    void testApi() {
        TestController controller = new TestController();
        new CGroup().add(components.property(TestA.class, x -> x.getA()).get().getComponent());
        new CGroup().add(components.instance(controller, c -> c.data().getA()).get().getComponent());
    }

    @Test
    public void testString() {
        EditComponentWrapper<String> wrapper = components.property(TestA.class, x -> x.getString()).get();
        CTextField component = (CTextField) wrapper.getComponent();
        wrapper.setValue("foo");
        Assert.assertEquals("foo", component.getText());
        component.setText("bar");
        assertEquals("bar", wrapper.getValue());
    }

    @Test
    public void testStringSpecial() {
        assertEquals("yeah", components.property(TestA.class, x -> x.getStringSpecial()).get().getValue());
    }

    @Test
    public void testType() {
        assertThat(components.type(String.class).get().getComponent(), instanceOf(CTextField.class));
    }
}
