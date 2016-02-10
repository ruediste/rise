package com.github.ruediste.rise.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class ComponentRequestInfo {

    private PageHandle pageHandle;

    private HttpRenderResult closePageResult;

    Map<BindingGroup<?>, Set<ConstraintViolation<?>>> initialConstraintViolationsMap;

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
        <T> void accept(BindingGroup<T> group,
                Set<ConstraintViolation<T>> violations);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void forEachInitialConstraintViolation(
            InitialConstraintViolationConsumer consumer) {
        if (initialConstraintViolationsMap != null) {
            initialConstraintViolationsMap.entrySet().forEach(e -> consumer
                    .accept((BindingGroup) e.getKey(), (Set) e.getValue()));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void addInitialContraintViolation(BindingGroup<T> group,
            Set<ConstraintViolation<T>> violations) {
        if (initialConstraintViolationsMap == null)
            initialConstraintViolationsMap = new HashMap<>();
        initialConstraintViolationsMap.put(group, (Set) violations);
    }
}
