package com.github.ruediste.rise.testApp.component;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Labeled;

public class TestPageDestructionController extends ControllerComponent {

    private int id;

    @Inject
    CoreRequestInfo info;

    @Inject
    ComponentPage page;

    @Inject
    DestructionRecoreder recorder;

    @Singleton
    public static class DestructionRecoreder {
        public Set<Integer> destroyed = new HashSet<>();

        public Object destroyed(int id) {
            return destroyed.add(id);
        }
    }

    @Labeled
    static class View extends ViewComponent<TestPageDestructionController> {

        @Override
        protected Component createComponents() {
            return new CPage().add(new CText(String.valueOf(controller.id)))
                    .add(new CButton(controller, x -> x.navigateAway()));
        }

    }

    @UrlUnsigned
    public ActionResult index(int id) {
        page.getDestroyEvent().addListener(x -> recorder.destroyed(id));
        this.id = id;
        return null;
    }

    @UrlUnsigned
    public ActionResult indexInvalidateSession(int id) {
        page.getDestroyEvent().addListener(x -> recorder.destroyed(id));
        this.id = id;
        info.getServletRequest().getSession().invalidate();
        return null;
    }

    @Labeled
    public void navigateAway() {
        redirect(go(TestPageDestructionController.class).index2());
    }

    public ActionResult index2() {
        return null;
    }
}
