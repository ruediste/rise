package laf.core.web.resource.v2;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.hash.Hashing;

public class ResourceGroupResolveNameTemplateTest {

	Resource resource;

	String resourceHash;

	ResourceGroup group;

	@Before
	public void setup() throws UnsupportedEncodingException {
		resource = new TestResourceImpl("test.js", "Hello".getBytes("UTF-8"));
		resourceHash = Hashing.sha256().hashBytes(resource.getData())
				.toString();
		group = new ResourceGroup((ResourceBundle) null, (List<Resource>) null);
	}

	@Test
	public void testResolveNameTemplate() throws Exception {
		assertEquals("foo.js", group.resolveNameTemplate(resource, "foo.js"));
		assertEquals(resourceHash + ".js",
				group.resolveNameTemplate(resource, "{hash}.js"));
		assertEquals("test/" + resourceHash + ".js",
				group.resolveNameTemplate(resource, "test/{hash}.js"));
		assertEquals("foo/test.css",
				group.resolveNameTemplate(resource, "foo/{name}.css"));
		resource = new TestResourceImpl("foo.bar.sass", resource.getData());
		assertEquals("foo/foo.bar.css",
				group.resolveNameTemplate(resource, "foo/{name}.css"));
		assertEquals("hell{o}",
				group.resolveNameTemplate(resource, "hell\\{o}"));
		assertEquals("hell\\o",
				group.resolveNameTemplate(resource, "hell\\\\o"));

	}

	@Test
	public void testResolveNameTemplateQualifiedName() {
		resource = new TestResourceImpl("foo/bar.css", resource.getData());
		assertEquals("static/foo/bar.js",
				group.resolveNameTemplate(resource, "static/{qname}.js"));

	}

	@Test
	public void testResolveNameTemplateExt() {
		resource = new TestResourceImpl("foo/bar.css", resource.getData());
		assertEquals("yeah.css",
				group.resolveNameTemplate(resource, "yeah.{ext}"));

	}
}
