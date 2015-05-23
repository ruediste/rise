package com.github.ruediste.rise.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.TransactionManager;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import org.slf4j.Logger;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.rise.api.ViewComponent;
import com.github.ruediste.rise.component.components.template.CWTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.ICoreUtil;
import com.github.ruediste.rise.core.persistence.TransactionCallbackNoResult;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRenderUtil;
import com.github.ruediste.rise.nonReloadable.persistence.TransactionControl;
import com.google.common.base.Charsets;

@Singleton
public class ComponentUtil implements ICoreUtil {

    @Inject
    Logger log;

    private final AttachedProperty<Component, Long> componentNr = new AttachedProperty<>();
    private final AttachedProperty<ViewComponent<?>, Map<Long, Component>> componentIdMap = new AttachedProperty<>();
    private final AttachedProperty<ViewComponent<?>, Long> maxComponentNr = new AttachedProperty<>();

    @Inject
    PageInfo pageInfo;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    AssetRenderUtil assetRenderUtil;

    @Inject
    ComponentConfiguration componentConfiguration;

    @Inject
    CoreUtil coreUtil;

    @Inject
    CoreRequestInfo coreRequestInfo;

    public long pageId() {
        return pageInfo.getPageId();
    }

    public String getKey(Component component, String key) {
        return "c_" + componentNr.get(component) + "_" + key;
    }

    public long getComponentNr(Component component) {
        return componentNr.get(component);
    }

    public Component getComponent(ViewComponent<?> view, long componentId) {
        return componentIdMap.get(view).get(componentId);
    }

    /**
     * Set the component number of all children of the root component which do
     * not have a number yet
     */
    public byte[] renderComponents(ViewComponent<?> view,
            Component rootComponent) {
        {
            // set the component IDs
            Map<Long, Component> map;
            if (componentIdMap.isSet(view)) {
                map = componentIdMap.get(view);
            } else {
                map = new HashMap<>();
                componentIdMap.set(view, map);
            }
            long nr;
            {
                Long tmp = maxComponentNr.get(view);
                if (tmp == null) {
                    tmp = 0L;
                }
                nr = tmp;
            }
            for (Component c : ComponentTreeUtil.subTree(rootComponent)) {
                if (!componentNr.isSet(c)) {
                    map.put(nr, c);
                    componentNr.set(c, nr++);
                }
            }
            maxComponentNr.set(view, nr);
        }
        // render the view first, to detect possible errors
        // before rendering the result
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
        OutputStreamWriter writer = new OutputStreamWriter(stream,
                Charsets.UTF_8);
        try {
            HtmlCanvas canvas = new HtmlCanvas(writer);
            render(rootComponent, canvas);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error while rendering component view",
                    e);
        }
        return stream.toByteArray();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void render(Component component, HtmlCanvas html) {
        try {
            ((CWTemplate) componentTemplateIndex.getTemplate(component
                    .getClass())).doRender(component, html);
        } catch (IOException e) {
            throw new RuntimeException("Error while rendering component", e);
        }
    }

    /**
     * Create a renderable rendering a component (including children)
     */
    public Renderable component(Component component) {
        return html -> render(component, html);
    }

    /**
     * Create a renderable rendering a component (including children)
     */
    public Renderable components(Iterable<Component> components) {
        return html -> components.forEach(c -> render(c, html));
    }

    /**
     * Return the appropriate value for the html element id attribute.
     */
    public String getComponentId(Component component) {
        return "c_" + getComponentNr(component);
    }

    public String getReloadUrl() {
        return coreUtil.url(componentConfiguration.getReloadPath() + "/"
                + pageId());
    }

    @Override
    public CoreUtil getCoreUtil() {
        return coreUtil;
    }

    public String getParameterValue(Component component, String key) {
        return coreRequestInfo.getRequest()
                .getParameter(getKey(component, key));
    }

    public boolean isParameterDefined(Component component, String key) {
        return coreRequestInfo.getRequest().getParameterMap()
                .containsKey(getKey(component, key));
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
    TransactionTemplate template;

    public void checkAndCommit(Runnable checker, Runnable inTransaction) {
        template.builder().updating().noNewEntityManagerSet()
                .execute(new TransactionCallbackNoResult() {

                    @Override
                    public void doInTransaction(TransactionControl trx) {

                        // run checker with separate EMs
                        if (checker != null) {
                            EntityManagerSet old = holder
                                    .getCurrentEntityManagerSet();
                            try {
                                checker.run();
                            } finally {
                                holder.closeCurrentEntityManagers();
                                holder.setCurrentEntityManagerSet(old);
                            }
                        }

                        holder.joinTransaction();

                        if (inTransaction != null) {
                            inTransaction.run();
                        }
                        // holder.flush();
                        trx.commit();
                    }
                });
    }
}
