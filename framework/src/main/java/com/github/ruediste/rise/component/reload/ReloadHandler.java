package com.github.ruediste.rise.component.reload;

import static java.util.stream.Collectors.toList;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;

import com.github.ruediste.rendersnakeXT.canvas.ByteArrayHtmlConsumer;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentPageHandleRepository;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.render.CanvasTargetFirstPass;
import com.github.ruediste.rise.component.tree.CHide;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.RootComponent;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Handler applying values, raising events and rendering the reloaded component
 */
public class ReloadHandler implements Runnable {
    @Inject
    Logger log;

    @Inject
    ComponentPage page;

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
    ComponentPageHandleRepository componentSessionInfo;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    Provider<CanvasTargetFirstPass> fistPassTargetProvider;

    @Inject
    ValidationUtil validationUtil;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void run() {

        log.debug("reloading page " + page.getPageId());

        coreRequestInfo.setObjectActionInvocation(page.getObjectActionInvocation());

        ViewComponentBase<?> view = page.getView();

        Component<?> reloadedComponent = util.getFragment(request.getFragmentNr());

        // parse the data
        List<Map<String, Object>> rawData;
        try (Reader in = new InputStreamReader(coreRequestInfo.getServletRequest().getInputStream(), Charsets.UTF_8)) {
            rawData = (List<Map<String, Object>>) new JSONParser().parse(in);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing request data", e);
        }

        Multimap<String, Object> data = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Map<String, Object> entry : rawData) {
            data.put((String) entry.get("name"), entry.get("value"));
        }

        request.setParameterData(data);

        // extract visible components
        List<Component<?>> reloadedVisibleComponents = reloadedComponent.subTree(x -> {
            if (x instanceof CHide) {
                return !((CHide) x).isHidden();
            }
            return true;
        });

        // apply request values
        for (Component<?> fragment : reloadedVisibleComponents) {
            ((IComponentTemplate) componentTemplateIndex.getTemplate(fragment).get()).applyValues(fragment);
        }

        // process actions
        for (Component<?> fragment : reloadedVisibleComponents) {
            ((IComponentTemplate) componentTemplateIndex.getTemplate(fragment).get()).processActions(fragment);
        }

        // check if a destination has been defined
        if (componentRequestInfo.getClosePageResult() != null) {
            componentSessionInfo.destroyCurrentPage();
            coreRequestInfo.setActionResult(componentRequestInfo.getClosePageResult());
        } else if (coreRequestInfo.getActionResult() == null) {

            // create root with previous children of the reloaded component
            RootComponent previousRoot = new RootComponent();
            previousRoot.getChildren().addAll(reloadedComponent.getChildren());
            previousRoot.getChildren().forEach(x -> x.setParent(previousRoot));
            previousRoot.setView(reloadedComponent.getView());

            // render result
            RiseCanvas<?> html = page.getCanvas();
            CanvasTargetFirstPass target = fistPassTargetProvider.get();
            target.captureStartStackTraces = coreConfiguration.doCaptureHtmlTagStartTraces();
            target.setPreviousRoot(previousRoot);
            target.setView(reloadedComponent.getView());
            html.setTarget(target);
            componentTemplateIndex.getTemplateRaw(reloadedComponent).get().doRender(reloadedComponent, html);
            target.commitAttributes();
            target.flush();
            target.checkAllTagsClosed();

            reloadedComponent.getChildren().clear();
            reloadedComponent.getChildren().addAll(target.getRoot().getChildren());

            // validation
            validationUtil.updateValidationPresenters();

            // produce output
            ByteArrayHtmlConsumer out = new ByteArrayHtmlConsumer();
            target.getProducers().forEach(p -> p.produce(out));

            coreRequestInfo.setActionResult(new ContentRenderResult(out.getByteArray(), r -> {
                r.setContentType(coreConfiguration.htmlContentType);

                List<String> pushedUrls = componentRequestInfo.getPushedUrls().stream()
                        .map(x -> x == null ? null : util.url(x)).collect(toList());
                r.addHeader("rise-pushed-urls", JSONArray.toJSONString(pushedUrls));
                if (view instanceof HttpServletResponseCustomizer) {
                    ((HttpServletResponseCustomizer) view).customizeServletResponse(r);
                }
            }));
        }
    }
}
