package com.github.ruediste.rise.component;

import java.util.HashMap;
import java.util.Map;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
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

    private final Map<Long, Component> componentNrMap = new MapMaker()
            .weakValues().makeMap();
    private long maxComponentNr = 0;

    private int nextEventHandlerId = 0;
    private Map<Integer, Runnable> eventHandlers = new HashMap<>();

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

    /**
     * Get an event handler based on it's id
     */
    public Runnable getEventHandler(int handlerId) {
        return eventHandlers.get(handlerId);
    }

    /**
     * Register an event handler.
     */
    public int registerEventHandler(Runnable handler) {
        int id = nextEventHandlerId++;
        eventHandlers.put(id, handler);
        return id;
    }

    public Map<Long, Component> getComponentNrMap() {
        return componentNrMap;
    }

    public long getNextComponentNr() {
        return maxComponentNr++;
    }
}