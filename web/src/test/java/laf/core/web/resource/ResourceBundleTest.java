package laf.core.web.resource;

import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceBundleTest {

	public static class TestResourceBundle extends ResourceBundle {
		@Inject
		ResourceOutput js;

		@Inject
		ResourceOutput css;

		@Override
		public void initializeImpl() {
			paths("test.css").load(servletContext())
					.process(processors.minifyCss()).send(css);
		}
	}

	@Mock
	ResourceOutput css;

	@Mock
	ServletContext servletContext;

	@Mock
	Processors processors;

	@InjectMocks
	TestResourceBundle bundle;

	private Resource sampleResource;

	@Before
	public void setup() throws UnsupportedEncodingException {
		when(servletContext.getResourceAsStream("test.css")).thenReturn(
				new ByteArrayInputStream("Hello".getBytes("UTF-8")));

		sampleResource = new TestResourceImpl("foo", "Hello");
		when(processors.minifyCss()).thenReturn(r -> sampleResource);
	}

	@Test
	public void testCss() {
		bundle.initialize(ResourceMode.DEVELOPMENT);

		verify(css).accept(sampleResource);
	}
}
