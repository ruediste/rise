package com.github.ruediste.rise.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.github.ruediste.rise.core.web.HttpRenderResult;

@RequestScoped
public class CoreRequestInfo {
    private HttpRequest request;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private HttpRenderResult actionResult;

    private ActionInvocation<String> stringActionInvocation;
    private ActionInvocation<Object> objectActionInvocation;

    private Throwable requestError;

    /**
     * Set after parsing a component or mvc request
     */
    public ActionInvocation<String> getStringActionInvocation() {
        return stringActionInvocation;
    }

    public void setStringActionInvocation(
            ActionInvocation<String> stringActionInvocation) {
        this.stringActionInvocation = stringActionInvocation;
    }

    public HttpRenderResult getActionResult() {
        return actionResult;
    }

    public void setActionResult(HttpRenderResult result) {
        this.actionResult = result;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public ServletContext getServletContext() {
        return servletRequest.getServletContext();
    }

    /**
     * Returns the error beeing handled during the evaluation of
     * {@link RequestErrorHandler#handle()}.
     */
    public Throwable getRequestError() {
        return requestError;
    }

    public void setRequestError(Throwable requestError) {
        this.requestError = requestError;
    }

    /**
     * Set immediately before calling the controller, after the transaction is
     * started, since loading arguments might require DB access.
     */
    public ActionInvocation<Object> getObjectActionInvocation() {
        return objectActionInvocation;
    }

    public void setObjectActionInvocation(
            ActionInvocation<Object> objectActionInvocation) {
        this.objectActionInvocation = objectActionInvocation;
    }

    public String getSessionId() {
        return getServletRequest().getSession().getId();
    }
}
