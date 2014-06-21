package laf.component.core;

import javax.inject.Inject;

import laf.component.TestComponentViewA;
import laf.component.TestComponentViewB1;
import laf.component.TestComponentViewB2;
import laf.component.TestControllerA;
import laf.component.TestControllerB;
import laf.component.TestViewQualifier1;
import laf.test.DeploymentProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ComponentViewRepositoryTest {

	@Deployment
	public static WebArchive createDeployment() {
		return DeploymentProvider.getDefault().addClasses(
				ComponentViewRepository.class, TestComponentViewA.class,
				TestComponentViewB1.class, TestComponentViewB2.class,
				ComponentView.class);
	}

	@Inject
	ComponentViewRepository componentViewRepository;

	@Test
	public void getSimple() {
		TestControllerA controllerA = new TestControllerA();
		ComponentView<TestControllerA> view = componentViewRepository
				.createView(controllerA);
		Assert.assertEquals(TestComponentViewA.class, view.getClass());
	}

	@Test
	public void getQualifier() {

		ComponentView<TestControllerB> view = componentViewRepository
				.createView(new TestControllerB(), TestViewQualifier1.class);
		Assert.assertTrue(view instanceof TestComponentViewB1);
	}
}
