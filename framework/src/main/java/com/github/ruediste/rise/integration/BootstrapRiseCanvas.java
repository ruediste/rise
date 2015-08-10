package com.github.ruediste.rise.integration;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste1.i18n.lString.TranslatedString;

public interface BootstrapRiseCanvas<TSelf extends BootstrapRiseCanvas<TSelf>>
        extends BootstrapCanvas<TSelf>, RiseCanvas<TSelf> {

    public default TSelf rButtonA(ActionResult target) {
        return rButtonA(target, false);
    }

    public default TSelf rButtonA(ActionResult target, boolean iconOnly) {
        return rButtonA(target, iconOnly, a -> {
        });
    }

    public default TSelf rButtonA(
            ActionResult target,
            Consumer<com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas.B_ButtonArgs> args) {
        return rButtonA(target, false, args);
    }

    public default TSelf rButtonA(
            ActionResult target,
            boolean iconOnly,
            Consumer<com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas.B_ButtonArgs> args) {
        RiseCanvasHelper helper = internal_riseHelper();
        ActionInvocation<Object> actionInvocation = helper.getUtil()
                .toActionInvocation(target);
        Method method = actionInvocation.methodInvocation.getMethod();

        TSelf result = bButtonA(args).HREF(
                helper.getUtil().url(
                        helper.getUtil().toPathInfo(actionInvocation)))
                .TEST_NAME(method.getName());
        TranslatedString label = helper.getLabelUtil().getMethodLabel(method);
        if (iconOnly) {
            return result.render(helper.getIconUtil().getIcon(method)).span()
                    .BsrOnly().content(label)._bButtonA();
        } else
            return result.fIfPresent(helper.getIconUtil().tryGetIcon(method),
                    this::render).content(label);
    }
}
