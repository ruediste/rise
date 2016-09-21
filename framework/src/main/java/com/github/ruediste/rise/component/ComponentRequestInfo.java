package com.github.ruediste.rise.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.UrlSpec;

@RequestScoped
public class ComponentRequestInfo {

    private PageHandle pageHandle;

    private HttpRenderResult closePageResult;

    private boolean isComponentRequest;

    private boolean isReloadRequest;

    private boolean isAjaxRequest;

    private boolean isInitialRequest;

    final private List<UrlSpec> pushedUrls = new ArrayList<>();

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

    public boolean isAjaxRequest() {
        return isAjaxRequest;
    }

    public void setAjaxRequest(boolean isAjaxRequest) {
        this.isAjaxRequest = isAjaxRequest;
    }

    public boolean isInitialRequest() {
        return isInitialRequest;
    }

    public void setInitialRequest(boolean isInitialRequest) {
        this.isInitialRequest = isInitialRequest;
    }

    public void pushUrl(UrlSpec url) {
        pushedUrls.add(url);
    }

    public void popUrl() {
        pushedUrls.add(null);
    }

    @FunctionalInterface
    public interface InitialConstraintViolationConsumer {
        void accept(Object controller, Set<ConstraintViolation<?>> violations);
    }

    public List<UrlSpec> getPushedUrls() {
        return pushedUrls;
    }

}
