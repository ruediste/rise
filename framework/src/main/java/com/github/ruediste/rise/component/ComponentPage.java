package com.github.ruediste.rise.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;
import com.google.common.collect.MapMaker;

/**
 * A Page of the component framework.
 * 
 * <p>
 * <b>Life Cycle </b><br>
 * A page is created before handling a request to a {@link ControllerComponent}.
 * The page lives until a reload requests sets the
 * {@link ComponentRequestInfo#getClosePageResult()} or until the heartbeat from
 * the page in the browser misses. If this happens, {@link #destroy()} is
 * called, notifying all listeners listening for the {@link #getDestroyEvent()}.
 */
@PageScoped
public class ComponentPage {

    private IControllerComponent controller;

    private ViewComponentBase<?> view;

    private long pageId;

    private EntityManagerSet entityManagerSet;

    private final GenericEventManager<Object> destroyEvent = new GenericEventManager<>();

    private final Map<Long, Component<?>> fragmentNrMap = new MapMaker().weakValues().makeMap();
    private long maxFragmentNr = 0;

    private ActionInvocation<Object> objectActionInvocation;

    private Component<?> root;

    private RiseCanvas<?> canvas;

    private final List<ValidationFailure> unhandledValidationFailures = new ArrayList<>();

    public IControllerComponent getController() {
        return controller;
    }

    public void setController(IControllerComponent controller) {
        this.controller = controller;
    }

    /**
     * Return the root view of the page
     */
    public ViewComponentBase<?> getView() {
        return view;
    }

    public void setView(ViewComponentBase<?> view) {
        this.view = view;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public ComponentPage self() {
        return this;
    }

    public EntityManagerSet getEntityManagerSet() {
        return entityManagerSet;
    }

    public void setEntityManagerSet(EntityManagerSet entityManagerSet) {
        this.entityManagerSet = entityManagerSet;
    }

    /**
     * Return an event which is fired when the page is destroyed. It is intended
     * to trigger the release of resources held by a page.
     * 
     * @see ComponentPageHandleRepository
     */
    public GenericEvent<Object> getDestroyEvent() {
        return destroyEvent.event();
    }

    public void destroy() {
        destroyEvent.fire(null);
    }

    public Map<Long, Component<?>> getComponentNrMap() {
        return fragmentNrMap;
    }

    public long getNextFragmentNr() {
        return maxFragmentNr++;
    }

    public ActionInvocation<Object> getObjectActionInvocation() {
        return objectActionInvocation;
    }

    public void setObjectActionInvocation(ActionInvocation<Object> objectActionInvocation) {
        this.objectActionInvocation = objectActionInvocation;
    }

    public Component<?> getRoot() {
        return root;
    }

    public void setRoot(Component<?> root) {
        this.root = root;

    }

    public RiseCanvas<?> getCanvas() {
        return canvas;
    }

    public void setCanvas(RiseCanvas<?> canvas) {
        this.canvas = canvas;
    }

    public List<ValidationFailure> getUnhandledValidationFailures() {
        return unhandledValidationFailures;
    }

}
