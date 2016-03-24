package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSelectTabs;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Labeled;

public class CSelectTabsController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<CSelectTabsController> {

        @Override
        protected Component createComponents() {
            CSelectTabs<String> tabs = new CSelectTabs<>();
            tabs.childRelation().add("foo");
            tabs.childRelation().add("bar");
            return new CPage().add(tabs).add(tabs.createPresenter());
        }

    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }
}
