package laf.core.web.resource;

import static org.junit.Assert.assertNotNull;
import laf.core.http.request.HttpRequestImpl;
import laf.core.requestParserChain.RequestParseResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class StaticWebResourceRequestHandlerTest {

	private Resource resource;
	private TestResourceBundle bundle;

	private class TestResourceBundle extends StaticWebResourceBundle {
		ResourceOutput js = new ResourceOutput(this);

		@Override
		public void initializeImpl() {
			js.accept(resource);
		}
	}

	@Mock
	Logger log;

	@InjectMocks
	StaticWebResourceRequestHandler handler;

	@Before
	public void setup() {
		resource = new TestResourceImpl("/foo", "bar");
		bundle = new TestResourceBundle();
		bundle.initializeImpl();

		handler.initialize(ResourceMode.PRODUCTION, bundle);
	}

	@Test
	public void testParse() {
		HttpRequestImpl request = new HttpRequestImpl("foo");

		RequestParseResult result = handler.parse(request);

		assertNotNull(result);
	}
}
