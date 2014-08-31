package laf.component.core;

public abstract class DelegatingRequestHandler<THandler, TDelegate> implements
		RequestHandler<THandler> {

	private RequestHandler<TDelegate> delegate;

	public RequestHandler<TDelegate> getDelegate() {
		return delegate;
	}

	public <T extends RequestHandler<TDelegate>> T setDelegate(T delegate) {
		this.delegate = delegate;
		return delegate;
	}
}
