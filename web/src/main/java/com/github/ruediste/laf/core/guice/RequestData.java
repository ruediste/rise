package com.github.ruediste.laf.core.guice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.entry.HttpMethod;
import com.google.inject.Key;

/**
 * Container for data associated with a request. A new instance is used for
 * every request
 */
public class RequestData {

	private final static ThreadLocal<RequestData> current = new ThreadLocal<RequestData>(){
		@Override
		protected RequestData initialValue() {
			return new RequestData(null, null, null);
		}
	};
	
	private HttpMethod method;

	public RequestData(HttpServletRequest req, HttpServletResponse resp,
			HttpMethod method) {
		request = req;
		response = resp;
		this.method = method;
	}

	public static void setCurrent(RequestData data) {
		current.set(data);
	}

	public static RequestData getCurrent() {
		return current.get();
	}

	public static void remove() {
		current.remove();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public final Map<Key<?>, Object> requestScopedInstances = new HashMap<>();
	public final HashSet<Key<?>> lockedRequestScopeKeys = new HashSet<>();

	private HttpServletRequest request;
	private HttpServletResponse response;

}
