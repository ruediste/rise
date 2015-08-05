package com.github.ruediste.rise.component;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class ComponentRequestInfo {

    private PageHandle pageHandle;

    private HttpRenderResult closePageResult;

    private boolean isComponentRequest;

    private boolean isReloadRequest;

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

    public boolean isReloadRequest() {
        return isReloadRequest;
    }

    public void setReloadRequest(boolean isReloadRequest) {
        this.isReloadRequest = isReloadRequest;
    }
}
