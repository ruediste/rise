package laf.httpRequest;

import java.util.Objects;

/**
 * Base implementation of {@link HttpRequest}, providing the
 * {@link #equals(Object)} implementation.
 */
public abstract class HttpRequestBase implements HttpRequest {

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HttpRequest)) {
			return false;
		}
		HttpRequest other = (HttpRequest) obj;

		return Objects.equals(getPath(), other.getPath())
				&& Objects.equals(getParameterMap(), other.getParameterMap());
	}

}