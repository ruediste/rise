package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;

/**
 * Render the action result set by the current request to the response
 */
public class ActionResultRenderer extends ChainedRequestHandler {

    @Inject
    CoreRequestInfo coreInfo;

    @Inject
    HttpRenderResultUtil util;

    @Override
    public void run(Runnable next) {
        next.run();
        try {
            HttpRenderResult actionResult = coreInfo.getActionResult();
            if (actionResult == null)
                throw new RuntimeException("Action result is null. Did you return null from your MVC Controller?");
            actionResult.sendTo(coreInfo.getServletResponse(), util);
        } catch (IOException e) {
            throw new RuntimeException("Error while sending result", e);
        }
    }
}