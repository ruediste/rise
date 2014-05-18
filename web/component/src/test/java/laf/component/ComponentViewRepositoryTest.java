package laf.component;

import javax.inject.Inject;

import laf.test.ComponentDeploymentProvider;

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
		return ComponentDeploymentProvider.getDefault().addClasses(
				ComponentViewRepository.class, TestComponentViewA.class,
				TestComponentViewB1.class, TestComponentViewB2.class,
				ComponentView.class);
	}

	@Inject
	ComponentViewRepository componentViewRepository;

	@Test
	public void getSimple() {
		ComponentView<TestControllerA> view = componentViewRepository
				.createView(TestControllerA.class);
		Assert.assertEquals(TestComponentViewA.class, view.getClass());
	}

	@Test
	public void getQualifier() {

		ComponentView<TestControllerB> view = componentViewRepository
				.createView(TestControllerB.class, TestViewQualifier1.class);
		Assert.assertTrue(view instanceof TestComponentViewB1);
	}
}
