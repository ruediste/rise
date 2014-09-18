package laf.core.web.resource;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import javax.servlet.ServletContext;

import laf.core.base.Pair;
import laf.core.http.CoreRequestInfo;

import org.apache.activemq.util.ByteArrayInputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IndividualResourceRequestHandlerTest {

	@Mock
	CoreRequestInfo coreRequestInfo;
	@Mock
	ServletContext ctx;

	@Mock
	Consumer<String> consumer;

	@InjectMocks
	private IndividualResourceRequestHandler handler;

	@Before
	public void setup() throws UnsupportedEncodingException {
		when(coreRequestInfo.getServletContext()).thenReturn(ctx);
		handler.initialize("ctxPre/", "urlPre/");
		when(
				coreRequestInfo.getServletContext().getResourceAsStream(
						"/ctxPre/test.css")).thenReturn(
				new ByteArrayInputStream("foo".getBytes("UTF-8")));
	}

	@Test
	public void simpleNoTransform() {

		handler.render(new ResourceBundle(new ResourceType("css"), "test.css"),
				consumer);

		verify(consumer).accept("urlPre/orig/test.css");
	}

	@Test
	public void simpleTransform() {

		handler.getResourceTransformers().put(
				Pair.of(new ResourceType("css"), new ResourceType("css1")),
				(in, out) -> {
				});
		handler.render(
				new ResourceBundle(new ResourceType("css1"), "test.css"),
				consumer);

		verify(consumer).accept("urlPre/transformed/test.css1");
	}

	@Test(expected = RuntimeException.class)
	public void simpleTransformNoTransformer() {

		handler.getResourceTransformers().put(
				Pair.of(new ResourceType("css"), new ResourceType("css2")),
				(in, out) -> {
				});
		handler.render(
				new ResourceBundle(new ResourceType("css1"), "test.css"),
				consumer);

	}

	@Test(expected = RuntimeException.class)
	public void resourceNotFound() throws UnsupportedEncodingException {
		when(
				coreRequestInfo.getServletContext().getResourceAsStream(
						anyString())).thenReturn(null);
		handler.render(new ResourceBundle(new ResourceType("css"), "test.css"),
				consumer);

	}
}
