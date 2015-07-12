package com.github.ruediste.rise.component;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.reload.PageReloadRequest;
import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;

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
    PageReloadRequest request;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    ComponentSessionInfo sessionInfo;

    @Inject
    @Named("pageScoped")
    SimpleProxyScopeHandler pageScopeHandler;

    private class AjaxParseResult implements RequestParseResult {

        private HttpRequest req;

        public AjaxParseResult(HttpRequest req) {
            this.req = req;
        }

        @Override
        public void handle() {
            componentRequestInfo.setComponentRequest(true);
            try {
                String suffix = req.getPathInfo().substring(
                        config.getAjaxPath().length());
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
                    pageNr = Long.parseLong(suffix.substring(0, idx));
                    suffix = suffix.substring(idx + 1);
                }

                PageHandle page = sessionInfo.getPageHandle(pageNr);

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
