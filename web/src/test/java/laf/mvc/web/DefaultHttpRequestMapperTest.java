package laf.mvc.web;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.Collections;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import laf.core.base.ActionResult;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;
import laf.mvc.core.ActionPath;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class DefaultHttpRequestMapperTest {

	private DefaultHttpRequestMapper mapper;
	private HttpRequestImpl request;

	public static class TestController {
		public ActionResult methodA() {
			return null;
		}

		public ActionResult methodB() {
			return null;
		}

		public ActionResult methodB(int i) {
			return null;
		}

		public void nonActionMethod() {

		}
	}

	@SuppressWarnings("rawtypes")
	@Before
	public void setup() {
		mapper = new DefaultHttpRequestMapper();
		mapper.log = mock(Logger.class);
		mapper.beanManager = mock(BeanManager.class);
		Bean beanMock = mock(Bean.class);
		when(mapper.beanManager.getBeans(any(Type.class), any())).thenReturn(
				Collections.singleton(beanMock));
		when(beanMock.getBeanClass()).thenReturn(TestController.class);
		request = new HttpRequestImpl();
		mapper.initialize(cls -> "test");
	}

	@Test
	public void testMethodA() {
		checkMapping("test.methodA", "methodA");
	}

	@Test
	public void testMethodB() {
		checkMapping("test.methodB", "methodB");
	}

	@Test
	public void testMethodBOverload() {
		checkMapping("test.methodB_1", "methodB");
	}

	@Test
	public void testInexisting() {
		request.setPath("foo");
		assertNull(mapper.parse(request));

		request.setPath("test.foo");
		assertNull(mapper.parse(request));
	}

	@Test
	public void testNonActionMethod() {
		request.setPath("test.methodA");
		assertNotNull(mapper.parse(request));
		request.setPath("test.nonActionMethod");
		assertNull(mapper.parse(request));
	}

	private void checkMapping(String expectedPath, String methodName) {
		request.setPath(expectedPath);
		ActionPath<String> path = mapper.parse(request);
		assertEquals(1, path.getElements().size());
		assertEquals(methodName, path.getFirst().getMethod().getName());

		HttpRequest request = mapper.generate(path);
		assertEquals(expectedPath, request.getPath());
	}

}
