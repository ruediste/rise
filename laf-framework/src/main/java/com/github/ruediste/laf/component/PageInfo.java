package com.github.ruediste.laf.component;

import java.util.Map;

import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.core.persistence.em.EntityManagerSet;
import com.github.ruediste.salta.core.Binding;

@PageScoped
public class PageInfo {

	private IComponentController controller;

	private CView<?> view;

	private Map<Binding, Object> pageScopedInstanceMap;

	private final Object lock = new Object();

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

	public void setValueMap(Map<Binding, Object> valueMap) {
		this.pageScopedInstanceMap = valueMap;

	}

	/**
	 * Map containing the page scoped values of this page. Used to re-enter the
	 * page scope
	 */
	public Map<Binding, Object> getPageScopedInstanceMap() {
		return pageScopedInstanceMap;
	}

	/**
	 * Lock used to guarantee single threaded access to a page
	 */
	public Object getLock() {
		return lock;
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
