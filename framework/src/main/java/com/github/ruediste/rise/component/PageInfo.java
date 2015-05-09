package com.github.ruediste.rise.component;

import com.github.ruediste.rise.api.CView;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;

@PageScoped
public class PageInfo {

	private IComponentController controller;

	private CView<?> view;

	private long pageId;

	private EntityManagerSet entityManagerSet;

	public IComponentController getController() {
		return controller;
	}

	public void setController(IComponentController controller) {
		this.controller = controller;
	}

	public CView<?> getView() {
		return view;
	}

	public void setView(CView<?> view) {
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
