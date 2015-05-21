package com.github.ruediste.rise.nonReloadable.front;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface StartupErrorHandler {

	void handle(Throwable t, HttpServletRequest request,
			HttpServletResponse response);

}
