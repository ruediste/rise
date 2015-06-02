package com.github.ruediste.rise.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.RiseCanvasBase;
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

    protected <T extends RiseCanvasBase<?>> void render(
            ByteArrayOutputStream stream, T canvas, Consumer<T> renderer) {
        canvas.initializeForOutput(stream);
        renderer.accept(canvas);
        canvas.flush();
    }

    /**
     * Render this by calling
     * {@link #render(ByteArrayOutputStream, RiseCanvasBase, Consumer)}
     */
    abstract public void render(ByteArrayOutputStream stream)
            throws IOException;

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