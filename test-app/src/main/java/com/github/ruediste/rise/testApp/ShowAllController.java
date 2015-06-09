package com.github.ruediste.rise.testApp;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.web.ActionPath;
import com.github.ruediste.rise.core.web.PathInfo;

public class ShowAllController extends ControllerMvc<ShowAllController> {

    private static class View extends ViewMvc<ShowAllController, Object> {

        @Inject
        PathInfoIndex idx;

        @Override
        public void render(TestCanvas html) {
            html.html().head()._head().body().h1().content("Exact").ul();

            for (String pathInfo : idx.getRegisteredPathInfos()) {
                html.li().a().HREF(new PathInfo(pathInfo)).content(pathInfo)
                        ._li();
            }
            html._ul().h1().content("Prefixes").ul();
            for (String pathInfo : idx.getRegisteredPrefixes()) {
                html.li().a().HREF(new PathInfo(pathInfo)).content(pathInfo)
                        ._li();
            }
            html._ul()._body()._html();
        }

    }

    @ActionPath(value = "/", primary = true)
    public ActionResult index() {
        return view(View.class, null);
    }
}
