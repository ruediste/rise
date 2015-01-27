package com.github.ruediste.laf.core.web.resource;

public class DelegatingResource implements Resource {

	private Resource delegate;

	DelegatingResource(Resource delegate) {
		this.delegate = delegate;

	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public byte[] getData() {
		return delegate.getData();
	}

	@Override
	public DataEqualityTracker getDataEqualityTracker() {
		return delegate.getDataEqualityTracker();
	}
}
