package com.github.ruediste.rise.component;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.component.components.ComponentTemplateBase;
import com.github.ruediste.rise.component.components.DefaultTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

public class ComponentTemplateIndexTest {

    ComponentTemplateIndex index;

    @Before
    public void setUp() throws Exception {
        index = new ComponentTemplateIndex();
    }

    @After
    public void tearDown() throws Exception {
    }

    private static class A extends Component<A> {
        static class Template extends ComponentTemplateBase<A> {

            @Override
            public void doRender(A component, RiseCanvas<?> html) {
            }
        }
    }

    private static class C extends A {
    }

    @DefaultTemplate(TemplateB.class)
    private static class B extends Component<B> {
    }

    static class TemplateB extends ComponentTemplateBase<B> {

        @Override
        public void doRender(B component, RiseCanvas<?> html) {
        }
    }

    @Test
    public void testExtractTemplate() throws Exception {
        assertEquals(Optional.of(A.Template.class), index.extractTemplate(A.class));
        assertEquals(Optional.of(TemplateB.class), index.extractTemplate(B.class));
        assertEquals(Optional.of(A.Template.class), index.extractTemplate(C.class));
    }

}
