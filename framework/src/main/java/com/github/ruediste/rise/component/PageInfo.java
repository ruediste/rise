package com.github.ruediste.rise.component;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;

@PageScoped
public class PageInfo {

    private IControllerComponent controller;

    private ViewComponentBase<?> view;

    private long pageId;

    private EntityManagerSet entityManagerSet;

    public IControllerComponent getController() {
        return controller;
    }

    public void setController(IControllerComponent controller) {
        this.controller = controller;
    }

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

    public PageInfo self() {
        return this;
    }

    public EntityManagerSet getEntityManagerSet() {
        return entityManagerSet;
    }

    public void setEntityManagerSet(EntityManagerSet entityManagerSet) {
        this.entityManagerSet = entityManagerSet;
    }

}