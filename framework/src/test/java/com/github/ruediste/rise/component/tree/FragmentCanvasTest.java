package com.github.ruediste.rise.component.tree;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.StringHtmlConsumer;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaRunner;
import com.github.ruediste.rise.util.Var;
import com.github.ruediste1.i18n.label.LabelUtil;

@RunWith(LambdaRunner.class)
public class FragmentCanvasTest {

    private static class TestCanvas extends RiseCanvasBase<TestCanvas>implements BootstrapRiseCanvas<TestCanvas> {

        @Override
        public TestCanvas self() {
            return this;
        }
    }

    TestCanvas html;

    @Before
    public void setUp() throws Exception {
        html = new TestCanvas();
        html.target = new FragmentCanvasTarget();
        html.target.labelUtil = new LabelUtil((str, locale) -> str.getFallback());
    }

    @Test
    public void testSimpleHtmlCreation() {
        html.html()._html();
        assertEquals("<html></html> ", getContents());
    }

    @Test
    public void testChangeableAttribute() {
        Var<String> var = new Var<>("abc");
        html.html().addAttribute("foo", var::getValue)._html();
        assertEquals("<html foo=\"abc\"></html> ", getContents());
        var.setValue("123");
        assertEquals("<html foo=\"123\"></html> ", getContents());
    }

    @Test
    public void testFforeach() {
        ArrayList<String> items = new ArrayList<>();
        items.add("foo");
        items.add("bar");

        html.html().ul().fForEach(() -> items, item -> html.li().content(item))._ul()._html();
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html><ul><li>foo</li> <li>bar</li> </ul> </html> ", getContents());

        items.add("fooBar");
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html><ul><li>foo</li> <li>bar</li> <li>fooBar</li> </ul> </html> ", getContents());
    }

    @Test
    public void testFIf() {
        Var<Boolean> condition = new Var<>(true);
        html.html().fIf(condition::getValue, () -> html.div()._div(), () -> html.span()._span())._html();
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html><div></div> </html> ", getContents());
        condition.setValue(false);
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html><span></span> </html> ", getContents());
    }

    @Test
    public void testFIfNested() {
        Var<Boolean> condition1 = new Var<>(true);
        Var<Boolean> condition2 = new Var<>(true);
        html.html().fIf(condition1::getValue, () -> html.write("true1").fIf(condition2::getValue,
                () -> html.write("true2"), () -> html.write("false2")), () -> html.write("false1"))._html();
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html>true1true2</html> ", getContents());
        condition2.setValue(false);
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html>true1false2</html> ", getContents());
        condition1.setValue(false);
        HtmlFragmentUtil.updateStructure(html.getTarget().getParentFragment());
        assertEquals("<html>false1</html> ", getContents());
    }

    @Test
    public void testFragmentProducerWorks() {
        html.write("foo");
        html.getTarget().flush();
        GroupHtmlFragment parentFragment = html.getTarget().getParentFragment();
        StringHtmlConsumer consumer = new StringHtmlConsumer();
        parentFragment.getHtmlProducer().produce(consumer);
        assertEquals("foo", consumer.getString());
    }

    @Test
    public void testToComponentAndRender() {
        Component fragment = html.toFragment(() -> html.div().ID("foo").CLASS("bar").content("div"));
        html.div().ID("id2").render(fragment)._div();
        assertEquals("<div id=\"id2\"><div id=\"foo\" class=\"bar\">div</div> </div> ", getContents());
    }

    private static class TestController extends SubControllerComponent {
        String data;

    }

    private static class ViewComponent extends FrameworkViewComponent<TestController> {

        @Override
        protected void renderImpl(BootstrapRiseCanvas<?> html) {
            html.input().VALUE(() -> controller.data);
        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testValueBinding() {
        ViewComponent view = new ViewComponent();
        TestController ctrl = new TestController();
        ctrl.data = "foo";
        html.internal_target().util = mock(ComponentUtil.class);

        view.config = mock(CoreConfiguration.class);
        when(view.config.createApplicationCanvas()).thenReturn((HtmlCanvas) html);
        when(html.internal_target().util.getParameterKey(Matchers.any(), Matchers.eq("value"))).thenReturn("c_0_value");

        view.initialize(ctrl);
        assertEquals("<input value=\"foo\" name=\"c_0_value\"> ", getContents(view.getRootFragment()));

        ctrl.data = "bar";
        assertEquals("<input value=\"bar\" name=\"c_0_value\"> ", getContents(view.getRootFragment()));
    }

    private String getContents() {
        FragmentCanvasTarget target = html.getTarget();
        target.flush();
        return getContents(target.getProducers());
    }

    private String getContents(Component fragment) {
        return getContents(Collections.singletonList(fragment.getHtmlProducer()));
    }

    private String getContents(List<HtmlProducer> producers) {
        StringHtmlConsumer consumer = new StringHtmlConsumer();
        producers.forEach(x -> x.produce(consumer));
        return consumer.getString();
    }

}
