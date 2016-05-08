package com.github.ruediste.rise.component.fragment;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rendersnakeXT.canvas.StringHtmlConsumer;
import com.github.ruediste.rise.util.Var;

public class FragmentCanvasTest {

    private static class TestCanvas extends FragmentCanvasBase<TestCanvas> {

        @Override
        public TestCanvas self() {
            return this;
        }
    }

    TestCanvas html;

    @Before
    public void setUp() throws Exception {
        html = new TestCanvas();

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

        html.html().ul().fForEach(items, item -> html.li().content(item))._ul()._html();
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

    private String getContents() {
        StringHtmlConsumer consumer = new StringHtmlConsumer();
        html.getTarget().flush();
        html.getTarget().getProducers().forEach(x -> x.produce(consumer));
        return consumer.getString();
    }

}
