package com.github.ruediste.rise.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class ComponentRequestInfo {

    private PageHandle pageHandle;

    private HttpRenderResult closePageResult;

    Map<Object, Set<ConstraintViolation<?>>> initialConstraintViolationsMap;

    private boolean isComponentRequest;

    private boolean isReloadRequest;

    private boolean isAjaxRequest;

    private boolean isInitialRequest;

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

    @FunctionalInterface
    public interface InitialConstraintViolationConsumer {
        void accept(Object controller, Set<ConstraintViolation<?>> violations);
    }

    public void forEachInitialConstraintViolation(InitialConstraintViolationConsumer consumer) {
        if (initialConstraintViolationsMap != null) {
            initialConstraintViolationsMap.entrySet().forEach(e -> consumer.accept(e.getKey(), e.getValue()));
        }
    }

    public void addInitialContraintViolations(Object controller, Set<? extends ConstraintViolation<?>> violations) {
        if (initialConstraintViolationsMap == null)
            initialConstraintViolationsMap = new HashMap<>();
        initialConstraintViolationsMap.computeIfAbsent(controller, x -> new HashSet<>()).addAll(violations);
    }
}
