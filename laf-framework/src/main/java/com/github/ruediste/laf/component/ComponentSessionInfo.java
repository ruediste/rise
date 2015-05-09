package com.github.ruediste.laf.component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.ruediste.laf.core.scopes.SessionScoped;

@SessionScoped
public class ComponentSessionInfo {

	private AtomicLong nextPageId = new AtomicLong();

	ConcurrentHashMap<Long, PageHandle> pageHandles = new ConcurrentHashMap<>();

	public PageHandle createPageHandle() {
		PageHandle handle = new PageHandle();
		handle.id = nextPageId.getAndIncrement();
		pageHandles.put(handle.id, handle);
		return handle;
	}

	public PageHandle getPageHandle(long id) {
		return pageHandles.get(id);
	}

	public void removePageHandle(long id) {
		pageHandles.remove(id);
	}
}
