package com.github.ruediste.laf.mvc.core;

public abstract class DelegatingRequestHandler<THandler, TDelegate> implements
		RequestHandler<THandler> {

	private RequestHandler<TDelegate> delegate;

	public RequestHandler<TDelegate> getDelegate() {
		return delegate;
	}

	public void setDelegate(RequestHandler<TDelegate> delegate) {
		this.delegate = delegate;
	}
}
