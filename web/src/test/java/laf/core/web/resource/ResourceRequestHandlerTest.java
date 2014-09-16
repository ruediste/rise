package laf.core.web.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.junit.Before;
import org.junit.Test;

public class ResourceRequestHandlerTest {

	private ResourceRequestHandler handler;
	private ServletContext ctx;
	private HttpServletResponse response;

	@Before
	public void setup() {
		handler = new ResourceRequestHandler();
		handler.coreRequestInfo = mock(CoreRequestInfo.class);
		ctx = mock(ServletContext.class);
		when(handler.coreRequestInfo.getServletContext()).thenReturn(ctx);

		response = mock(HttpServletResponse.class);
		when(handler.coreRequestInfo.getServletResponse()).thenReturn(response);
	}

	@Test
	public void noBundles() {
		handler.initialize("assets/", "static/", false);
		StringBuilder sb = new StringBuilder();
		handler.render(new ResourceBundle(ResourceType.JS, "foo.js", "bar.js"),
				s -> sb.append(s + ", "));
		assertEquals("static/foo.js, static/bar.js, ", sb.toString());
	}

	@Test
	public void bundles() throws IOException {
		when(ctx.getResourceAsStream("/assets/foo.js")).thenReturn(
				new ByteArrayInputStream("foo".getBytes("UTF-8")));
		when(ctx.getResourceAsStream("/assets/bar.js")).thenReturn(
				new ByteArrayInputStream("bar".getBytes("UTF-8")));
		handler.initialize("assets/", "static/", true);

		// check render
		StringBuilder sb = new StringBuilder();
		handler.render(new ResourceBundle(ResourceType.JS, "foo.js", "bar.js"),
				s -> sb.append(s));
		String expectedPath = "static/bundles/c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2.js";
		assertEquals(expectedPath, sb.toString());

		// check request handling
		HttpRequest request = new HttpRequestImpl(expectedPath);

		ByteArrayServletOutputStream baos = new ByteArrayServletOutputStream();
		when(response.getOutputStream()).thenReturn(baos);
		handler.parse(request).handle(request);
		assertEquals("foobar", new String(baos.toByteArray(), "UTF-8"));
	}
}
