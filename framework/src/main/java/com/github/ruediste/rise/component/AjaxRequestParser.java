package com.github.ruediste.rise.component;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.HttpRenderResultUtil;
import com.github.ruediste.salta.standard.util.SimpleScopeManagerBase.ScopeState;

/**
 * Request handling ajax update requests form client side components to the
 * component template on the server
 */
public class AjaxRequestParser implements RequestParser {

    @Inject
    Logger log;

    @Inject
    ComponentConfiguration config;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    ComponentPageHandleRepository sessionInfo;

    @Inject
    ComponentUtil componentUtil;

    @Inject
    ComponentPage pageInfo;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    PageScopeManager pageScopeHandler;

    @Inject
    HttpRenderResultUtil httpRenderResultUtil;

    @Inject
    CoreRequestInfo coreRequestInfo;

    private class AjaxParseResult implements RequestParseResult {

        private HttpRequest req;

        public AjaxParseResult(HttpRequest req) {
            this.req = req;
        }

        @Override
        public void handle() {
            componentRequestInfo.setComponentRequest(true);
            componentRequestInfo.setAjaxRequest(true);
            try {
                String suffix = req.getPathInfo().substring(config.getAjaxPath().length());
                if (suffix.startsWith("/"))
                    suffix = suffix.substring(1);
                long pageNr;
                {
                    int idx = suffix.indexOf('/');
                    pageNr = Long.parseLong(suffix.substring(0, idx));
                    suffix = suffix.substring(idx + 1);
                }
                long componentNr;
                {
                    int idx = suffix.indexOf('/');
                    if (idx < 0)
                        idx = suffix.length();
                    componentNr = Long.parseLong(suffix.substring(0, idx));
                    if (idx < suffix.length())
                        suffix = suffix.substring(idx + 1);
                    else
                        suffix = "";
                }

                // get the component
                PageHandle page = sessionInfo.getPageHandle(pageNr);
                componentRequestInfo.setPageHandle(page);
                HtmlFragment fragment;
                synchronized (page.lock) {
                    ScopeState old = pageScopeHandler.setState(page.pageScopeState);
                    try {
                        fragment = componentUtil.getFragment(componentNr);
                    } finally {
                        pageScopeHandler.setState(old);
                    }
                }
                try {
                    HttpRenderResult renderResult = fragment.handleAjaxRequest(suffix);
                    if (renderResult != null)
                        renderResult.sendTo(coreRequestInfo.getServletResponse(), httpRenderResultUtil);
                } catch (Throwable e) {
                    throw new RuntimeException("Error while handling ajax request", e);
                }
            } finally {
                componentRequestInfo.setComponentRequest(false);
            }
        }
    }

    @Override
    public RequestParseResult parse(HttpRequest req) {
        return new AjaxParseResult(req);
    }
}
