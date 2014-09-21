package laf.core.web.resource.v2;

import org.junit.Test;

public class ResourceUtilTest {

	@Test
	public void test() {
		ResourceContext ctx = null;
		ResourceOutput<ResourceJs> out = null;

		DataSources.classPath("foo.js", "bar.js")
				.toResourceGroup(ctx, ResourceJs::new)
				.fork(x -> x.dev().send(out)).prod()
				.collect("{hash}.js", ResourceJs::new).send(out);

		DataSources
				.servletContext("foo.js")
				.toResourceGroup(ctx, ResourceJs::new)
				.dev()
				.merge(DataSources.servletContext("foo.min.js")
						.toResourceGroup(ctx, ResourceJs::new).prod())
				.name("foo.js", ResourceJs::new);
	}
}
