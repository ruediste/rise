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
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.reload.PageReloadRequest;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
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
        return "c_" + componentNr.get(component) + "_" + key;
    }

    public long getComponentNr(Component component) {
        return componentNr.get(component);
    }

    public Component getComponent(ViewComponentBase<?> view, long componentId) {
        return pageInfo.getComponentNrMap().get(componentId);
    }

    /**
     * Render a component and all its children
     */
    public byte[] renderComponents(ComponentPage page, Component rootComponent) {
        // Set the component number of all children of the root component which
        // do not have a number yet. This allows components to reference each
        // other in the generated view without caring about the rendering order.
        {
            // set the component IDs
            ComponentPage p = page.self();
            for (Component c : ComponentTreeUtil.subTree(rootComponent)) {
                if (!componentNr.isSet(c)) {
                    long nr = p.getNextComponentNr();
                    p.getComponentNrMap().put(nr, c);
                    componentNr.set(c, nr++);
                }
            }
        }
        // render the view first, to detect possible errors
        // before rendering the result
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void render(Component component, RiseCanvas<?> canvas) {
        ((IComponentTemplate) componentTemplateIndex.getTemplate(component.getClass())).doRender(component, canvas);
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

}
