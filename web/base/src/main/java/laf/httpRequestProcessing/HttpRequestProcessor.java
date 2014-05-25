package laf.httpRequestProcessing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpRequestProcessor {
	void process(HttpServletRequest request, HttpServletResponse response);
}