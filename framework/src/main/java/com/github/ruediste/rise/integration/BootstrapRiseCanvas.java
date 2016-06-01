package com.github.ruediste.rise.integration;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss;
import com.github.ruediste.rendersnakeXT.canvas.FuncCanvas;
import com.github.ruediste.rise.component.fragment.FragmentCanvas;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste1.i18n.lString.TranslatedString;

public interface BootstrapRiseCanvas<TSelf extends BootstrapRiseCanvas<TSelf>>
        extends BootstrapCanvas<TSelf>, RiseCanvas<TSelf>, FuncCanvas<TSelf>, FragmentCanvas<TSelf> {

    public default TSelf rButtonA(ActionResult target) {
        return rButtonA(target, a -> {
        });
    }

    public default TSelf rButtonA(ActionResult target, Consumer<R_ButtonArgs> args) {
        RiseCanvasHelper helper = internal_riseHelper();
        ActionInvocation<Object> actionInvocation = helper.getUtil().toActionInvocation(target);
        Method method = actionInvocation.methodInvocation.getMethod();

        R_ButtonArgs buttonArgs = new R_ButtonArgs(this, true);
        args.accept(buttonArgs);

        Object instance = internal_riseHelper()
                .getControllerAuthzInstance(actionInvocation.methodInvocation.getInstanceClass());
        boolean authorized = helper.getAuthz().isAuthorized(instance, actionInvocation.methodInvocation.getMethod(),
                actionInvocation.methodInvocation.getArguments());

        if (!authorized && buttonArgs.nonAuthorizedHidden) {
            return self();
        }

        TSelf result = bButtonA(() -> buttonArgs);

        if (!authorized && !buttonArgs.nonAuthorizedNormal) {
            buttonArgs.disabled();
        }

        if (buttonArgs.disabled)
            result.HREF("#");
        else {
            result.HREF(target);
        }

        result.TEST_NAME(method.getName());
        TranslatedString label = helper.getLabelUtil().method(method).label();
        if (buttonArgs.iconOnly) {
            return result.render(helper.getIconUtil().getIcon(method)).span().BsrOnly().content(label)._bButtonA();
        } else
            return result.fIfPresent(helper.getIconUtil().tryGetIcon(method), this::render).content(label);
    }

    public static class R_ButtonArgs extends B_ButtonArgs<R_ButtonArgs> {

        protected R_ButtonArgs(BootstrapCanvasCss<?> html, boolean isLink) {
            super(html, isLink);
        }

        boolean iconOnly;
        boolean disabled;
        boolean nonAuthorizedHidden;
        boolean nonAuthorizedNormal;

        public R_ButtonArgs iconOnly() {
            iconOnly = true;
            return this;
        }

        public R_ButtonArgs iconOnly(boolean value) {
            iconOnly = value;
            return this;
        }

        public R_ButtonArgs nonAuthorizedHidden() {
            return nonAuthorizedHidden(true);
        }

        /**
         * By default, buttons targeting controller methods for which the
         * current user is not authorized are disabled. By setting this to true,
         * such buttons are hidden
         */
        public R_ButtonArgs nonAuthorizedHidden(boolean value) {
            nonAuthorizedHidden = value;
            return this;
        }

        public R_ButtonArgs nonAuthorizedNormal() {
            return nonAuthorizedNormal(true);
        }

        /**
         * By default, buttons targeting controller methods for which the
         * current user is not authorized are disabled. By setting this to true,
         * such buttons are shown normal
         */
        public R_ButtonArgs nonAuthorizedNormal(boolean value) {
            nonAuthorizedNormal = value;
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
