package laf.core.web.resource.v2;

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
}
