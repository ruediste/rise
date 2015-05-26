package com.github.ruediste.rise.api;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.mvc.MvcUtil;
import com.google.common.reflect.TypeToken;

/**
 * Base Class for views of the MVC framework
 */
public abstract class ViewMvcBase<TController extends IControllerMvc, TData> {

    @Inject
    private MvcUtil util;

    private TData data;

    private Class<? extends TController> controllerClass;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ViewMvcBase() {
        controllerClass = (Class) TypeToken.of(getClass())
                .resolveType(ViewMvcBase.class.getTypeParameters()[0])
                .getRawType();
    }

    public void setControllerClass(Class<? extends TController> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public final void initialize(TData data) {
        this.data = data;
    }

    public final TData data() {
        return data;
    }

    /**
     * Render this view to the provided canvas
     */
    abstract public void render(HtmlCanvasTarget htmlTarget) throws IOException;

    public ActionInvocationBuilderKnownController<? extends TController> path() {
        return util.path(controllerClass);
    }

    public <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return util.path(controllerClass);
    }

    public <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    public TController go() {
        return util.go(controllerClass);
    }

    public String url(ActionResult path) {
        return util.url(path);
    }

    public String url(PathInfo path) {
        return util.url(path);
    }

}