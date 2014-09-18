package laf.core.web.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BundleResourceRequestHandlerTest {

	@Mock
	private ServletContext ctx;

	@Mock
	CoreRequestInfo coreRequestInfo;

	@Mock
	HttpServletResponse response;

	@InjectMocks
	BundleResourceRequestHandler handler;

	@Before
	public void setup() {
		when(coreRequestInfo.getServletContext()).thenReturn(ctx);

		when(coreRequestInfo.getServletResponse()).thenReturn(response);
	}

	@Test
	public void bundles() throws IOException {
		when(ctx.getResourceAsStream("/assets/foo.js")).thenReturn(
				new ByteArrayInputStream("foo".getBytes("UTF-8")));
		when(ctx.getResourceAsStream("/assets/bar.js")).thenReturn(
				new ByteArrayInputStream("bar".getBytes("UTF-8")));
		handler.initialize("assets/", "static/");

		// check render
		StringBuilder sb = new StringBuilder();
		handler.render(new ResourceBundle(ResourceType.JS, "foo.js",
				"bar.js"), s -> sb.append(s));
		String expectedPath = "static/c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2.js";
		assertEquals(expectedPath, sb.toString());

		// check request handling
		HttpRequest request = new HttpRequestImpl(expectedPath);

		ByteArrayServletOutputStream baos = new ByteArrayServletOutputStream();
		when(response.getOutputStream()).thenReturn(baos);
		handler.parse(request).handle(request);
		assertEquals("foobar", new String(baos.toByteArray(), "UTF-8"));
	}
}
