package com.github.ruediste.rise.component.render;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.github.ruediste.rendersnakeXT.canvas.StringHtmlConsumer;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;
import com.github.ruediste.rise.integration.RiseCanvasHelper;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class RiseCanvasTargetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private static class Canvas extends RiseCanvasBase<Canvas> {

        @Override
        public Canvas self() {
            return this;
        }

    }

    @Test
    public void testSimpleRendering() {
        assertEquals("<div><p>Hello World</p> </div> ", render(html -> html.div().p().content("Hello World")._div()));
    }

    @Test(expected = RuntimeException.class)
    public void testSimpleRenderingTagsClosed() {
        render(html -> html.div().p().content("Hello World"));
    }

    @Test
    public void testPlaceholderRendering() {
        assertEquals("<div><span>hello world</span> </div> ",
                render(html -> html.div().addPlaceholder(() -> html.span().content("hello world"))._div()));
    }

    @Test(expected = RuntimeException.class)
    public void testPlaceholderTagCloseVerification() {
        render(html -> html.div().addPlaceholder(() -> html.span())._div());
    }

    @Test(expected = RuntimeException.class)
    public void testPlaceholderCannotStartWithAttribute() {
        render(html -> html.div().addPlaceholder(() -> html.CLASS("foo"))._div());
    }

    @Test
    public void testAtributePlaceholderRendering() {
        assertEquals("<div disabled=\"\"></div> ",
                render(html -> html.div().addAttributePlaceholder(() -> html.DISABLED())._div()));
    }

    @Test(expected = RuntimeException.class)
    public void testAtributePlaceholderRenderingTagsFail() {
        render(html -> html.div().addAttributePlaceholder(() -> html.div()._div())._div());
    }

    public static String render(Consumer<RiseCanvas<?>> renderer) {
        CanvasTargetFirstPass target = renderFirstPass(renderer);

        StringHtmlConsumer consumer = new StringHtmlConsumer();
        target.getProducers().forEach(p -> p.produce(consumer));
        String actual = consumer.getString();
        return actual;
    }

    public static CanvasTargetFirstPass renderFirstPass(Consumer<RiseCanvas<?>> renderer) {
        return renderFirstPass(renderer, null);
    }

    public static CanvasTargetFirstPass renderFirstPass(Consumer<RiseCanvas<?>> renderer, Component<?> previousRoot) {
        Injector injector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                bindMock(RiseCanvasHelper.class);
                bindMock(Logger.class);
                bindMock(ComponentUtil.class);
            }

            private <T> void bindMock(Class<T> cls) {
                bind(cls).toProvider(() -> mock(cls));
            }
        });
        Canvas html = injector.getInstance(Canvas.class);
        CanvasTargetFirstPass target = injector.getInstance(CanvasTargetFirstPass.class);
        target.setPreviousRoot(previousRoot);
        html.setTarget(target);
        renderer.accept(html);
        target.commitAttributes();
        target.flush();
        target.checkAllTagsClosed();
        return target;
    }
}
