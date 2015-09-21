package com.github.ruediste.rise.component.reload;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentSessionInfo;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;

/**
 * Handler applying values, raising events and rendering the reloaded component
 */
public class ReloadHandler implements Runnable {
    @Inject
    Logger log;

    @Inject
    PageInfo page;

    @Inject
    ComponentUtil util;

    @Inject
    PageReloadRequest request;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    CoreRequestInfo coreRequestInfo;
    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    ComponentSessionInfo componentSessionInfo;
    @Inject
    CoreConfiguration coreConfiguration;

    @Override
    public void run() {

        log.debug("reloading page " + page.getPageId());

        ViewComponentBase<?> view = page.getView();

        Component reloadComponent = util.getComponent(view,
                request.getComponentNr());

        // apply request values
        List<Component> components = ComponentTreeUtil.subTree(reloadComponent);

        for (Component c : components) {
            componentTemplateIndex.getTemplate(c).applyValues(c);
        }

        // raise events
        for (Component c : components) {
            componentTemplateIndex.getTemplate(c).raiseEvents(c);
        }

        // check if a destination has been defined
        if (componentRequestInfo.getClosePageResult() != null) {
            componentSessionInfo.removePageHandle(page.getPageId());
            coreRequestInfo
                    .setActionResult(componentRequestInfo.getClosePageResult());
        } else {
            // render result
            coreRequestInfo.setActionResult(new ContentRenderResult(
                    util.renderComponents(view, reloadComponent), r -> {
                        r.setContentType(coreConfiguration.htmlContentType);
                        if (view instanceof HttpServletResponseCustomizer) {
                            ((HttpServletResponseCustomizer) view)
                                    .customizeServletResponse(r);
                        }
                    }));
        }
    }
}
