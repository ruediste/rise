package laf.core.web.resource.v2;

import static org.junit.Assert.assertNotNull;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;
import laf.core.requestParserChain.RequestParseResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceRequestHandlerTest {

	private Resource resource;
	private TestResourceBundle bundle;

	private class TestResourceBundle extends ResourceBundle {
		ResourceOutput js = new ResourceOutput(this);

		public void initialize() {
			js.accept(resource);
		}
	}

	@InjectMocks
	ResourceRequestHandler handler;

	@Before
	public void setup() {
		resource = new TestResourceImpl("foo", "bar");
		bundle = new TestResourceBundle();
		bundle.initialize();

		handler.initialize(bundle);
	}

	@Test
	public void testParse() {
		HttpRequestImpl request = new HttpRequestImpl("foo");

		RequestParseResult<HttpRequest> result = handler.parse(request);

		assertNotNull(result);
	}
}
