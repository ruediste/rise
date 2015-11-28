package com.github.ruediste.rise.testApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.ActionPath;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste1.i18n.label.Labeled;

public class ShowAllController extends ControllerMvc<ShowAllController> {

    @Labeled
    private static class View extends ViewMvc<ShowAllController, Object> {

        @Inject
        PathInfoIndex idx;

        @Override
        public void renderContent(TestCanvas html) {
            html.h1().content("Exact").ul();
            List<String> pathInfos = new ArrayList<>();
            for (String pathInfo : idx.getRegisteredPathInfos()) {
                pathInfos.add(pathInfo);
            }
            Collections.sort(pathInfos);
            for (String pathInfo : pathInfos)
                html.li().a().HREF(new UrlSpec(new PathInfo(pathInfo)))
                        .content(pathInfo)._li();

            html._ul().h1().content("Prefixes").ul();
            for (String pathInfo : idx.getRegisteredPrefixes()) {
                html.li().a().HREF(new UrlSpec(new PathInfo(pathInfo)))
                        .content(pathInfo)._li();
            }
            html._ul();
        }

    }

    @UrlUnsigned
    @ActionPath(value = "/", primary = true)
    public ActionResult index() {
        return view(View.class, null);
    }
}
