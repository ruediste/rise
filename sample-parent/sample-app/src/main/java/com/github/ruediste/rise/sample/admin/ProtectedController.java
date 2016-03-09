package com.github.ruediste.rise.sample.admin;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.sample.PageView;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.front.RequiresRight;
import com.github.ruediste.rise.sample.front.SampleRight;
import com.github.ruediste1.i18n.label.Labeled;

public class ProtectedController extends ControllerMvc<ProtectedController> {

    @Labeled
    public static class View extends PageView<ProtectedController, String> {

        @Override
        public void renderContent(SampleCanvas html) {
            html.write(data());
        }

    }

    @Labeled
    @RequiresRight(SampleRight.VIEW_USER_PAGE)
    public ActionResult protectedUserPage() {
        return view(View.class, "Page accessible to users and admins");
    }

    @Labeled
    @RequiresRight(SampleRight.VIEW_ADMIN_PAGE)
    public ActionResult protectedAdminPage() {
        return view(View.class, "Page accessible to admins only");
    }
}
