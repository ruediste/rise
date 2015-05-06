package com.github.ruediste.laf.component;

import java.util.concurrent.atomic.AtomicLong;

import com.github.ruediste.laf.core.scopes.SessionScoped;

@SessionScoped
public class ComponentSessionInfo {

	private AtomicLong nextPageId = new AtomicLong();

	public long takePageId() {
		return nextPageId.getAndIncrement();
	}
}
