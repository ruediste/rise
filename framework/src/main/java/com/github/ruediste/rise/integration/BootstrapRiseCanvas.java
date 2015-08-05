package com.github.ruediste.rise.integration;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

public interface BootstrapRiseCanvas<TSelf extends BootstrapRiseCanvas<TSelf>>
        extends BootstrapCanvas<TSelf>, RiseCanvas<TSelf> {

    public default TSelf rButtonA(ActionResult target) {
        return rButtonA(target, a -> {
        });
    }

    public default TSelf rButtonA(
            ActionResult target,
            Consumer<com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas.B_ButtonArgs> args) {
        RiseCanvasHelper helper = internal_riseHelper();
        ActionInvocation<Object> actionInvocation = helper.getUtil()
                .toActionInvocation(target);
        Method method = actionInvocation.methodInvocation.getMethod();

        return bButtonA(args)
                .HREF(helper.getUtil().url(
                        helper.getUtil().toPathInfo(actionInvocation)))
                .TEST_NAME(method.getName())
                .fIfPresent(helper.getIconUtil().tryGetIcon(method),
                        this::render)
                .content(helper.getLabelUtil().getMethodLabel(method));
    }
}
