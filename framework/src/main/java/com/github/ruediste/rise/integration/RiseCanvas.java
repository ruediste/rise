package com.github.ruediste.rise.integration;

import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.security.authorization.Authz;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;
import com.github.ruediste.rise.util.MethodInvocation;
import com.github.ruediste1.i18n.lString.LString;

public interface RiseCanvas<TSelf extends RiseCanvas<TSelf>>
        extends Html5Canvas<TSelf>, FuncCanvas<TSelf> {

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

    default TSelf add(Component c) {
        internal_riseHelper().add(c);
        return self();
    }

    /**
     * Write the supplied buffer directly to the output, but commit attributes
     * beforehand
     */
    default TSelf writeRaw(byte[] buffer) {
        internal_riseHelper().writeRaw(buffer);
        return self();
    }

    default TSelf TITLE(LString value) {
        return TITLE(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf content(LString value) {
        return content(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf content(Enum<?> value) {
        return content(
                internal_riseHelper().getLabelUtil().getEnumMemberLabel(value));
    }

    default TSelf write(LString value) {
        return write(value.resolve(internal_riseHelper().getCurrentLocale()));
    }

    default TSelf render(Component c) {
        internal_riseHelper().renderComponent(c, this);
        return self();
    }

    default TSelf renderChildren(Component parent) {
        for (Component c : parent.getChildren())
            internal_riseHelper().renderComponent(c, this);
        return self();
    }

    /**
     * Add a "data-test-name" attribute to the current element. The name can be
     * used to locate elements in selenium tests. It is only written if
     * {@link CoreConfiguration#isRenderTestName()}, which is by default the
     * case in all stages except {@link ProjectStage#PRODUCTION}.
     * 
     * @param name
     *            test name to render. If null or empty, the attribute will be
     *            omitted
     */
    default TSelf TEST_NAME(String name) {
        internal_riseHelper().TEST_NAME(name);
        return self();
    }

    default TSelf rIfAuthorized(ActionResult target,
            Consumer<ActionResult> ifTrue) {
        return rIfAuthorized(target, ifTrue, x -> {
        });
    }

    default TSelf rIfAuthorized(ActionResult target,
            Consumer<ActionResult> ifTrue, Consumer<ActionResult> ifFalse) {
        MethodInvocation<Object> invocation = internal_riseHelper().getUtil()
                .toActionInvocation(target).methodInvocation;
        Object targetObj = internal_riseHelper()
                .getControllerAuthzInstance(invocation.getInstanceClass());
        if (Authz.isAuthorized(targetObj, invocation.getMethod(),
                invocation.getArguments().toArray()))
            ifTrue.accept(target);
        else
            ifFalse.accept(target);

        return self();
    }
}
