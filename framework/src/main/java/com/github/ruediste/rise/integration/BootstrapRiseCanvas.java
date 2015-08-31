package com.github.ruediste.rise.integration;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.security.authorization.Authz;
import com.github.ruediste1.i18n.lString.TranslatedString;

public interface BootstrapRiseCanvas<TSelf extends BootstrapRiseCanvas<TSelf>>
        extends BootstrapCanvas<TSelf>, RiseCanvas<TSelf> {

    public default TSelf rButtonA(ActionResult target) {
        return rButtonA(target, a -> {
        });
    }

    public default TSelf rButtonA(ActionResult target,
            Consumer<R_ButtonArgs> args) {
        RiseCanvasHelper helper = internal_riseHelper();
        ActionInvocation<Object> actionInvocation = helper.getUtil()
                .toActionInvocation(target);
        Method method = actionInvocation.methodInvocation.getMethod();

        R_ButtonArgs buttonArgs = new R_ButtonArgs(this, true);

        TSelf result = bButtonA(() -> {
            args.accept(buttonArgs);
            Object instance = internal_riseHelper().getInstance(
                    actionInvocation.methodInvocation.getInstanceClass());
            if (!Authz.isAuthorized(instance,
                    actionInvocation.methodInvocation.getMethod(),
                    actionInvocation.methodInvocation.getArguments())) {
                buttonArgs.disabled();
            }
            return buttonArgs;
        });
        if (buttonArgs.disabled)
            result.HREF("#");
        else
            result.HREF(helper.getUtil().url(
                    helper.getUtil().toPathInfo(actionInvocation)));

        result.TEST_NAME(method.getName());
        TranslatedString label = helper.getLabelUtil().getMethodLabel(method);
        if (buttonArgs.iconOnly) {
            return result.render(helper.getIconUtil().getIcon(method)).span()
                    .BsrOnly().content(label)._bButtonA();
        } else
            return result.fIfPresent(helper.getIconUtil().tryGetIcon(method),
                    this::render).content(label);
    }

    public static class R_ButtonArgs extends B_ButtonArgs<R_ButtonArgs> {

        protected R_ButtonArgs(BootstrapCanvasCss<?> html, boolean isAnchor) {
            super(html, isAnchor);
        }

        boolean iconOnly;
        boolean disabled;

        public R_ButtonArgs iconOnly() {
            iconOnly = true;
            return this;
        }

        public R_ButtonArgs iconOnly(boolean value) {
            iconOnly = value;
            return this;
        }

        @Override
        public R_ButtonArgs disabled() {
            super.disabled();
            disabled = true;
            return this;
        }
    }

}
