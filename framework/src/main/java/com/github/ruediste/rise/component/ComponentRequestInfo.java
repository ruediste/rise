package com.github.ruediste.rise.component;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class ComponentRequestInfo {

    private PageHandle pageHandle;

    private HttpRenderResult closePageResult;

    public HttpRenderResult getClosePageResult() {
        return closePageResult;
    }

    public void setClosePageResult(HttpRenderResult closePageResult) {
        this.closePageResult = closePageResult;
    }

    public boolean isComponentRequest() {
        return isComponentRequest;
    }

    public void setComponentRequest(boolean isComponentRequest) {
        this.isComponentRequest = isComponentRequest;
    }

    public PageHandle getPageHandle() {
        return pageHandle;
    }

    public void setPageHandle(PageHandle pageHandle) {
        this.pageHandle = pageHandle;
    }

    private boolean isComponentRequest;
}
