package com.github.ruediste.rise.component;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.reload.PageReloadRequest;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.ICoreUtil;
import com.github.ruediste.rise.core.persistence.TransactionCallbackNoResult;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;
import com.github.ruediste.salta.jsr330.Injector;

@Singleton
public class ComponentUtil implements ICoreUtil {

    @Inject
    Logger log;

    private final AttachedProperty<Component, Long> componentNr = new AttachedProperty<>();

    private final AttachedProperty<Component, Long> renderNr = new AttachedProperty<>();

    @Inject
    ComponentPage pageInfo;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    ComponentConfiguration componentConfiguration;

    @Inject
    CoreUtil coreUtil;
    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    PageScopeManager pageScopeHandler;

    @Inject
    PageReloadRequest reloadRequest;

    @Inject
    Injector injector;

    public long pageId() {
        return pageInfo.getPageId();
    }

    public String getKey(Component component, String key) {
        return "c_" + getComponentNr(component) + "_" + key;
    }

    public long getComponentNr(Component component) {
        Long result = componentNr.get(component);
        if (result == null) {
            // lazily set the component numbers. This allows components to
            // reference each other without caring about
            // the rendering order.
            result = pageInfo.getNextComponentNr();
            componentNr.set(component, result);
            pageInfo.getComponentNrMap().put(result, component);
        }
        return result;
    }

    public Component getComponent(long componentId) {
        return pageInfo.getComponentNrMap().get(componentId);
    }

    /**
     * Render a component and all its children
     */
    public byte[] renderComponents(ComponentPage page, Component rootComponent) {

        // render the view to a buffer to detect errors before starting to send
        // the response
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
        RiseCanvasBase<?> html = coreConfiguration.createApplicationCanvas();
        html.initializeForOutput(stream);
        try {
            render(rootComponent, html);
            html.flush();
        } catch (Throwable t) {
            throw new RuntimeException("Error while rendering component view", t);
        }
        return stream.toByteArray();
    }

    /**
     * Render a component to the given canvas
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void render(Component component, RiseCanvas<?> canvas) {
        renderNr.set(component, pageInfo.getRenderNr());
        ((IComponentTemplate) componentTemplateIndex.getTemplate(component.getClass()).get()).doRender(component,
                canvas);
    }

    /**
     * Create a renderable rendering a component (including children)
     */
    public Renderable<RiseCanvas<?>> component(Component component) {
        return html -> render(component, html);
    }

    /**
     * Create a renderable rendering a component (including children)
     */
    public Renderable<RiseCanvas<?>> components(Iterable<Component> components) {
        return html -> components.forEach(c -> render(c, html));
    }

    /**
     * Return the appropriate value for the html element id attribute.
     */
    public String getComponentId(Component component) {
        return "c_" + getComponentNr(component);
    }

    public String getReloadUrl() {
        return coreUtil.url(componentConfiguration.getReloadPath() + "/" + pageId());
    }

    public String getAjaxUrl(Component component) {
        return coreUtil.url(componentConfiguration.getAjaxPath() + "/" + pageId() + "/" + getComponentNr(component));
    }

    @Override
    public CoreUtil getCoreUtil() {
        return coreUtil;
    }

    public Optional<Object> getParameterObject(Component component, String key) {
        return reloadRequest.getParameterObject(getKey(component, key));
    }

    /**
     * Return the value of a parameter belonging to a certain component during a
     * page reload request.
     */
    public Optional<String> getParameterValue(Component component, String key) {
        return reloadRequest.getParameterValue(getKey(component, key));
    }

    public Collection<Object> getParameterObjects(Component component, String key) {
        return reloadRequest.getParameterObjects(getKey(component, key));
    }

    public List<String> getParameterValues(Component component, String key) {
        return reloadRequest.getParameterValues(getKey(component, key));
    }

    /**
     * Test if a parameter is defined for a certain component during a page
     * reload request.
     */
    public boolean isParameterDefined(Component component, String key) {
        return reloadRequest.isParameterDefined(getKey(component, key));
    }

    public void commit() {
        checkAndCommit(null, null);
    }

    public void commit(Runnable inTransaction) {
        checkAndCommit(null, inTransaction);
    }

    public void checkAndCommit(Runnable checker) {
        checkAndCommit(checker, null);
    }

    @Inject
    TransactionManager txm;

    @Inject
    EntityManagerHolder holder;

    @Inject
    TransactionControl template;

    public void checkAndCommit(Runnable checker, Runnable inTransaction) {
        template.updating().execute(new TransactionCallbackNoResult() {

            @Override
            public void doInTransaction() {

                // run checker with separate EMs
                if (checker != null) {
                    template.forceNewEntityManagerSet().execute(checker::run);
                }

                holder.joinTransaction();

                if (inTransaction != null) {
                    inTransaction.run();
                }
            }
        });
    }

    /**
     * Instead of rendering this page again, close it an use the given result to
     * render the response.
     */
    public void closePage(HttpRenderResult closePageResult) {
        componentRequestInfo.setClosePageResult(closePageResult);
    }

    /**
     * Run the given runnable in the page scope of the current request. Mainly
     * useful in ajax request handling code. Note that only a single thread can
     * enter the scope of a page. Do not lock the page for extended periods of
     * time (like long running search queries)
     */
    public void runInPageScope(Runnable r) {
        PageHandle pageHandle = componentRequestInfo.getPageHandle();
        synchronized (pageHandle.lock) {
            pageScopeHandler.inScopeDo(pageHandle.pageScopeState, r);
        }
    }

    /**
     * Return true if the page has been rendered in the last render phase
     */
    public boolean wasRendered(Component component) {
        // renderNr of the page is incremented immediately before rendering
        return ((Long) pageInfo.getRenderNr()).equals(renderNr.get(component));
    }
}
