package laf.urlMapping;

import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ControllerInfoRepositoryInitializer;
import laf.controllerInfo.impl.TestController;

import org.apache.commons.lang.NotImplementedException;
import org.junit.*;

import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;

public class DefaultUrlMappingRuleTest {

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	DefaultUrlMappingRule rule;

	@ObjectUnderTest
	ActionPathFactory factory;

	@InjectIntoMany
	@ObjectUnderTest
	ControllerInfoRepository repo = new ControllerInfoRepository();

	@InjectIntoMany
	@ObjectUnderTest
	ControllerInfoRepositoryInitializer initializer = new ControllerInfoRepositoryInitializer();

	@Before
	public void init() {
		initializer.putControllerInfo(TestController.class, false);
		rule.initialize();
	}

	@Test
	public void generate() {
		PathActionResult path = (PathActionResult) factory.createActionPath(
				TestController.class).actionMethod(1);

		rule.generate(path);

		throw new NotImplementedException();
	}
}
