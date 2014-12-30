package laf.core.translation.message;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.inject.Inject;

import laf.core.translation.PString;
import laf.core.translation.TString;
import laf.test.DeploymentProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TMessageInvocationHandlerTest {

	@Deployment
	public static WebArchive createDeployment() {
		return DeploymentProvider
				.getDefault()
				.addAsLibraries(
						Maven.resolver()
								.loadPomFromFile("pom.xml")
								.resolve(
										"org.apache.deltaspike.modules:deltaspike-partial-bean-module-impl",
										"org.apache.deltaspike.modules:deltaspike-partial-bean-module-api")
								.withTransitivity().asFile())
				.addPackage(TMessageInvocationHandler.class.getPackage());
	}

	@TMessages
	public static interface TestMessages {
		PString noItemFound();

		TString noItemFound1();

		TString tStringWithArgs(int arg);

		int wrongReturnType();

		@TMessage("There are {count} users")
		PString userCount(int count);
	}

	@Inject
	TestMessages msgs;

	@Test
	public void testTStringResult() {
		assertEquals(

				new TString(
						"laf.core.translation.message.TMessageInvocationHandlerTest$TestMessages.noItemFound1",
						"No Item Found1."), msgs.noItemFound1());

	}

	@Test(expected = RuntimeException.class)
	public void testWrongReturnType() {
		msgs.wrongReturnType();
	}

	@Test(expected = RuntimeException.class)
	public void testTStringWithArgs() {
		msgs.tStringWithArgs(4);
	}

	@Test
	public void testMessages() {
		HashMap<String, Object> map = new HashMap<>();
		assertEquals(
				new PString(
						new TString(
								"laf.core.translation.message.TMessageInvocationHandlerTest$TestMessages.noItemFound",
								"No Item Found."), map), msgs.noItemFound());

		map.put("count", 4);
		assertEquals(
				new PString(
						new TString(
								"laf.core.translation.message.TMessageInvocationHandlerTest$TestMessages.userCount",
								"There are {count} users"), map),
				msgs.userCount(4));
	}
}
