package com.github.ruediste.rise.component.render;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rendersnakeXT.canvas.StringHtmlConsumer;
import com.github.ruediste.rise.integration.RiseCanvasBase;

public class RiseCanvasTargetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private class Canvas extends RiseCanvasBase<Canvas> {

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

    private String render(Consumer<Canvas> renderer) {
        Canvas html = new Canvas();
        CanvasTargetFirstPass target = new CanvasTargetFirstPass();
        html.setTarget(target);
        renderer.accept(html);
        target.commitAttributes();
        target.flush();
        target.checkAllTagsClosed();
        StringHtmlConsumer consumer = new StringHtmlConsumer();
        target.getProducers().forEach(p -> p.produce(consumer));
        String actual = consumer.getString();
        return actual;
    }
}
