package com.github.ruediste.rise.testApp.security;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.testApp.RequiresRight;
import com.github.ruediste.rise.testApp.Right;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.ViewMvc;
import com.github.ruediste1.i18n.label.Labeled;

public class ProtectedMethodsController extends
        ControllerMvc<ProtectedMethodsController> {

    @Labeled
    public static class IndexView extends
            ViewMvc<ProtectedMethodsController, String> {

        @Override
        protected void renderContent(TestCanvas html) {
            html.rButtonA(go().methodAllowed())
                    .rButtonA(go().methodForbidden());
        }
    }

    @Labeled
    public static class MethodView extends
            ViewMvc<ProtectedMethodsController, String> {

        @Override
        protected void renderContent(TestCanvas html) {
            html.span().TEST_NAME("data").content(data());
        }

    }

    public ActionResult index() {
        return view(IndexView.class, "");
    }

    @Labeled
    @RequiresRight(Right.ALLOWED)
    public ActionResult methodAllowed() {
        return view(MethodView.class, "success");
    }

    @Labeled
    @RequiresRight(Right.FORBIDDEN)
    public ActionResult methodForbidden() {
        return view(MethodView.class, "success");
    }
}
