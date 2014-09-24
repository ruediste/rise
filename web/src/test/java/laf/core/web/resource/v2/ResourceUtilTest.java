package laf.core.web.resource.v2;

import org.junit.Test;

public class ResourceUtilTest {

	@Test
	public void test() {
		ResourceContext ctx = null;
		ResourceOutput out = null;

		ctx.classPath("foo.js", "bar.js").fork(x -> x.dev().send(out)).prod()
				.collect("{hash}.js").send(out);

		ctx.servletContext("foo.js").dev()
				.merge(ctx.servletContext("foo.min.js").prod()).name("foo.js");
	}
}
