package laf.requestProcessing;


public abstract class DelegatingRequestProcessor implements RequestProcessor {

	private RequestProcessor delegate;

	public RequestProcessor getDelegate() {
		return delegate;
	}

	public void initialize(RequestProcessor delegate) {
		this.delegate = delegate;
	}

}
