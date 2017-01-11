package com.github.ruediste.rise.integration;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.IViewQualifier;
import com.github.ruediste.rise.component.components.CValue;
import com.github.ruediste.rise.component.components.CView;
import com.github.ruediste.rise.component.components.ConClick;
import com.github.ruediste.rise.component.render.ComponentCanvas;
import com.github.ruediste.rise.component.render.RiseCanvasTarget;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste1.i18n.lString.LString;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public interface RiseCanvas<TSelf extends RiseCanvas<TSelf>> extends ComponentCanvas<TSelf>, FuncCanvas<TSelf> {

    RiseCanvasHelper internal_riseHelper();

    /**
     * Render a css link for all css assets in the given output
     */
    default TSelf rCssLinks(AssetBundle output) {
        internal_riseHelper().rCssLinks(this, output);
        return self();
    }

    /**
     * Render a js link for all js assets in the given output
     */
    default TSelf rJsLinks(AssetBundle output) {
        internal_riseHelper().rJsLinks(this, output);
        return self();
    }

    default TSelf HREF(ActionResult destination) {
        return HREF(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf HREF(UrlSpec destination) {
        return HREF(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf ACTION(ActionResult destination) {
        return ACTION(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf ACTION(UrlSpec destination) {
        return ACTION(internal_riseHelper().getUtil().url(destination));
    }

    default TSelf TITLE(LString value) {
        return TITLE(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf content(LString value) {
        return content(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf content(Enum<?> value) {
        return content(internal_riseHelper().getLabelUtil().enumMember(value).label());
    }

    default TSelf write(LString value) {
        return write(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    /**
     * Add a "data-test-name" attribute to the current element. The name can be
     * used to locate elements in selenium tests. It is only written if
     * {@link CoreConfiguration#isRenderTestName()}, which is by default the
     * case in all stages except {@link ApplicationStage#PRODUCTION}.
     * 
     * @param name
     *            test name to render. If null or empty, the attribute will be
     *            omitted
     */
    default TSelf TEST_NAME(String name) {
        internal_riseHelper().TEST_NAME(this, name);
        return self();
    }

    default TSelf TEST_NAME(Optional<String> name) {
        name.ifPresent(s -> TEST_NAME(s));
        return self();
    }

    default TSelf TEST_NAME(ActionResult name) {
        return TEST_NAME(((ActionInvocationResult) name).methodInvocation.getMethod().getName());
    }

    default TSelf rIfAuthorized(ActionResult target, Consumer<ActionResult> ifTrue) {
        return rIfAuthorized(target, ifTrue, x -> {
        });
    }

    default TSelf rIfAuthorized(ActionResult target, Consumer<ActionResult> ifTrue, Consumer<ActionResult> ifFalse) {
        if (internal_riseHelper().util.isAutorized(target))
            ifTrue.accept(target);
        else
            ifFalse.accept(target);

        return self();
    }

    public enum JavaScriptEvent {
        focusin, focusout, click
    }

    default TSelf rCOMPONENT_ATTRIBUTES(Component<?> component) {
        return self().CLASS(component.CLASS()).TEST_NAME(component.TEST_NAME()).fIf(component.isDisabled(),
                () -> DISABLED());
    }

    default TSelf renderView(ViewComponentBase<?> view) {
        return add(new CView(view));
    }

    default TSelf renderController(SubControllerComponent controller) {
        return add(new CView(controller));
    }

    default TSelf renderController(Object controller, Class<? extends IViewQualifier> viewQualifier) {
        internal_riseHelper().renderController(this, controller, viewQualifier);
        return self();
    }

    default TSelf writeFromClasspath(Class<?> cls, String name) {
        try {
            return writeUnescaped(new String(ByteStreams.toByteArray(cls.getResourceAsStream(name)), Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    RiseCanvasTarget internal_target();

    void setTarget(RiseCanvasTarget target);

    default TSelf addPlaceholder(Runnable placeholder) {
        internal_target().addPlaceholder(this, placeholder);
        return self();
    }

    default TSelf addAttributePlaceholder(Runnable placeholder) {
        internal_target().addAttributePlaceholder(this, placeholder);
        return self();
    }

    default TSelf render(Component<?> component) {
        return add(component);
    }

    default TSelf add(Component<?> component) {
        internal_target().add(this, component);
        return self();
    }

    default TSelf RonClick(Runnable handler) {
        return add(new ConClick(handler));
    }

    default TSelf Rvalue(ValueHandle<String> value) {
        return add(new CValue(value));
    }

    default TSelf Rvalue(@Capture Supplier<String> value) {
        return add(new CValue(value));
    }

    default TSelf Rvalue(@Capture Supplier<String> value, boolean isLabelProperty) {
        return add(new CValue(value, isLabelProperty));
    }

    /**
     * Return the current parent fragment
     * 
     * @return
     */
    default Component<?> getParent() {
        return internal_target().getParent();
    }
}
