package com.github.ruediste.rise.component.render;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.HidingComponent;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CanvasTargetFirstPassTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testComponentRendering() {
        assertEquals("<div><div class=\"parent\"><div class=\"child\">foo</div> </div> </div> ",
                RiseCanvasTargetTest
                        .render(html -> html.div()
                                .add(new TestComponent().CLASS("parent")
                                        .body(() -> html
                                                .add(new TestComponent().CLASS("child").body(() -> html.write("foo")))))
                        ._div()));
    }

    @Test
    public void testComponentRenderingHidden() {
        assertEquals("<div><div class=\"parent\"><div class=\"child\">foo</div> </div> </div> ",
                RiseCanvasTargetTest.render(renderHidden(false, "")));
        assertEquals("<div><div class=\"parent\"></div> </div> ", RiseCanvasTargetTest.render(renderHidden(true, "")));
        Component<?> root = RiseCanvasTargetTest.renderFirstPass(renderHidden(false, "Hello World")).getRoot();
        root = RiseCanvasTargetTest.renderFirstPass(renderHidden(true, "foo"), root).getRoot();
        root = RiseCanvasTargetTest.renderFirstPass(renderHidden(true, "bar"), root).getRoot();
        root = RiseCanvasTargetTest.renderFirstPass(renderHidden(false, "fooBar"), root).getRoot();
        TestComponent child = (TestComponent) root.getChildren().get(0).getChildren().get(0).getChildren().get(0);
        assertEquals("Hello World", child.getStateValueString());
    }

    private Consumer<RiseCanvas<?>> renderHidden(boolean hide, String value) {
        return html -> html.div().add(new TestComponent().CLASS("parent")
                .body(() -> html.add(new HidingComponent().hidden(hide).content(() -> html.add(
                        new TestComponent().setStateValueString(value).CLASS("child").body(() -> html.write("foo")))))))
                ._div();
    }

    @Test
    public void testTreeConstruction() {
        Component<?> root = RiseCanvasTargetTest
                .renderFirstPass(
                        html -> html.div()
                                .add(new TestComponent().CLASS("parent")
                                        .body(() -> html
                                                .add(new TestComponent().CLASS("child").body(() -> html.write("foo")))))
                ._div()).getRoot();
        assertEquals(1, root.getChildren().size());
        Component<?> c = root.getChildren().get(0);
        assertEquals("parent", c.CLASS());
        assertEquals(1, c.getChildren().size());
        c = c.getChildren().get(0);
        assertEquals("child", c.CLASS());
    }

    @Test
    public void testStateMerging() {
        Component<?> firstRoot = RiseCanvasTargetTest
                .renderFirstPass(html -> render(html, "foo", Optional.of("Hello World"))).getRoot();
        Component<?> root = RiseCanvasTargetTest
                .renderFirstPass(html -> render(html, "bar", Optional.empty()), firstRoot).getRoot();
        TestComponent component = (TestComponent) root.getChildren().get(0).getChildren().get(0);
        assertEquals("foo", component.getStateValueString());
        assertEquals(Optional.of("Hello World"), component.getStateValueOptional());
    }

    private void render(RiseCanvas<?> html, String stateValueString, Optional<String> stateValueOptional) {
        html.div().add(new TestComponent().CLASS("parent")
                .body(() -> html.add(new TestComponent().setStateValueString(stateValueString)
                        .setStateValueOptional(stateValueOptional).CLASS("child").body(() -> html.write("foo")))))
                ._div();
    }
}
