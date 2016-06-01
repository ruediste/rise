package com.github.ruediste.rise.integration;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.IViewQualifier;
import com.github.ruediste.rise.component.fragment.FragmentCanvas;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.util.MethodInvocation;
import com.github.ruediste1.i18n.lString.LString;

public interface RiseCanvas<TSelf extends RiseCanvas<TSelf>>
        extends Html5Canvas<TSelf>, FuncCanvas<TSelf>, FragmentCanvas<TSelf> {

    RiseCanvasHelper internal_riseHelper();

    /**
     * Render a css link for all {@link DefaultAssetTypes#CSS} assets in the
     * given output
     */
    default TSelf rCssLinks(AssetBundleOutput output) {
        internal_riseHelper().rCssLinks(this, output);
        return self();
    }

    /**
     * Render a js link for all {@link DefaultAssetTypes#JS} assets in the given
     * output
     */
    default TSelf rJsLinks(AssetBundleOutput output) {
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

    @Deprecated
    default TSelf render(Component c) {
        return self();
    }

    @Deprecated
    default TSelf renderChildren(Component parent) {
        return self();
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
        return fIfPresent(name, s -> self().TEST_NAME(s));
    }

    default TSelf TEST_NAME(ActionResult name) {
        return TEST_NAME(((ActionInvocationResult) name).methodInvocation.getMethod().getName());
    }

    default TSelf rIfAuthorized(ActionResult target, Consumer<ActionResult> ifTrue) {
        return rIfAuthorized(target, ifTrue, x -> {
        });
    }

    default TSelf rIfAuthorized(ActionResult target, Consumer<ActionResult> ifTrue, Consumer<ActionResult> ifFalse) {
        MethodInvocation<Object> invocation = internal_riseHelper().getUtil()
                .toActionInvocation(target).methodInvocation;
        Object targetObj = internal_riseHelper().getControllerAuthzInstance(invocation.getInstanceClass());
        if (internal_riseHelper().getAuthz().isAuthorized(targetObj, invocation.getMethod(),
                invocation.getArguments().toArray()))
            ifTrue.accept(target);
        else
            ifFalse.accept(target);

        return self();
    }

    public enum JavaScriptEvent {
        focusin, focusout, click
    }

    default TSelf rCOMPONENT_ATTRIBUTES(ComponentBase<?> component) {
        return self().CLASS(component.CLASS()).TEST_NAME(component.TEST_NAME()).fIf(component.isDisabled(),
                () -> DISABLED());
    }

    default TSelf addView(ViewComponentBase<?> view) {
        internal_target().addFragmentAndRender(view.getRootFragment());
        return self();
    }

    default TSelf addController(Object controller) {
        internal_riseHelper().addController(this, controller);
        return self();
    }

    default TSelf addController(Object controller, Class<? extends IViewQualifier> viewQualifier) {
        internal_riseHelper().addController(this, controller, viewQualifier);
        return self();
    }

    default TSelf add(Component c) {
        internal_riseHelper().add(this, c);
        return self();
    }
}
