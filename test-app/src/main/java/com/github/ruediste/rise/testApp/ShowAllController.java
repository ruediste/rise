package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.web.ActionPath;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste1.i18n.label.Labeled;

public class ShowAllController extends ControllerMvc<ShowAllController> {

    @Labeled
    private static class View extends ViewMvc<ShowAllController, Object> {

        @Inject
        PathInfoIndex idx;

        @Override
        public void renderContent(TestCanvas html) {
            html.h1().content("Exact").ul();

            for (String pathInfo : idx.getRegisteredPathInfos()) {
                html.li().a().HREF(new PathInfo(pathInfo)).content(pathInfo)
                        ._li();
            }
            html._ul().h1().content("Prefixes").ul();
            for (String pathInfo : idx.getRegisteredPrefixes()) {
                html.li().a().HREF(new PathInfo(pathInfo)).content(pathInfo)
                        ._li();
            }
            html._ul();
        }

    }

    @ActionPath(value = "/", primary = true)
    public ActionResult index() {
        return view(View.class, null);
    }
}
