package laf.core.web.resource.v2;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.hash.Hashing;

public class ResourceBaseTest {

	private static class TestResource extends ResourceBase<TestResource> {

		public TestResource(String name, byte[] data) {
			super(name, data);
		}

	}

	TestResource resource;

	String resourceHash;

	@Before
	public void setup() throws UnsupportedEncodingException {
		resource = new TestResource("test.js", "Hello".getBytes("UTF-8"));
		resourceHash = Hashing.sha256().hashBytes(resource.data).toString();
	}

	@Test
	public void testResolveNameTemplate() throws Exception {
		assertEquals("foo.js", resource.resolveNameTemplate("foo.js"));
		assertEquals(resourceHash + ".js",
				resource.resolveNameTemplate("{hash}.js"));
		assertEquals("test/" + resourceHash + ".js",
				resource.resolveNameTemplate("test/{hash}.js"));
		assertEquals("foo/test.css",
				resource.resolveNameTemplate("foo/{name}.css"));
		resource = new TestResource("foo.bar.sass", resource.data);
		assertEquals("foo/foo.bar.css",
				resource.resolveNameTemplate("foo/{name}.css"));

	}

}
