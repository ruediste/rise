package com.github.ruediste.rise.api;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.integration.RiseCanvasBase;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.reflect.TypeToken;

/**
 * Base Class for views of the MVC framework.
 * 
 * <p>
 * Since each application will use it's own {@link HtmlCanvas} subclass, the
 * type is passed as generic parameter. The class is instantiated by the DI
 * framework (salta) and passed to {@link #render(RiseCanvasBase)}.
 */
public abstract class ViewMvcBase<TController extends IControllerMvc, TData, TCanvas extends RiseCanvasBase<?>> {

    @Inject
    private MvcUtil util;

    @Inject
    Provider<TCanvas> canvasProvider;

    @Inject
    LabelUtil labelUtil;

    @Inject
    CurrentLocale currentLocale;

    private TData data;

    private Class<? extends TController> controllerClass;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ViewMvcBase() {
        controllerClass = (Class) TypeToken.of(getClass()).resolveType(ViewMvcBase.class.getTypeParameters()[0])
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

    public void render(ByteArrayOutputStream out) {
        TCanvas canvas = canvasProvider.get();
        canvas.initializeForOutput(out);
        render(canvas);
        canvas.flush();
    }

    protected abstract void render(TCanvas html);

    public ActionInvocationBuilderKnownController<? extends TController> path() {
        return util.path(controllerClass);
    }

    public <T extends IController> ActionInvocationBuilderKnownController<T> path(Class<T> controllerClass) {
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

    public LString label(Class<?> clazz) {
        return labelUtil.type(clazz).label();
    }

    public LString label(Object obj) {
        return labelUtil.type(obj.getClass()).label();
    }

    public LString label(Enum<?> enumMember) {
        return labelUtil.enumMember(enumMember).label();
    }

    public String resolve(LString lstr) {
        return lstr.resolve(currentLocale.getCurrentLocale());
    }
}