package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.integration.RiseCanvas.JavaScriptEvent;
import com.github.ruediste1.i18n.label.Label;

public class TestEventController extends ControllerComponent {

    @Label("Events")
    public static class View extends ViewComponent<TestEventController> {

        @Override
        protected Component createComponents() {
            return new CPage()
                    .render(toComponent(html -> html.span().TEST_NAME("eventSpan").rON(JavaScriptEvent.click, () -> {
                        controller.eventCount++;
                    }).content("fooBar"))).render(toComponentDirect(html -> html.addFragmentAndRender("eventCount: ").span()
                            .TEST_NAME("eventCount").content(String.valueOf(controller.eventCount))));

        }

    }

    int eventCount;

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }

}
