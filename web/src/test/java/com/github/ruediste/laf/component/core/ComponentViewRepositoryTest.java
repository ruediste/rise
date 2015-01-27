package com.github.ruediste.laf.component.core;

import javax.inject.Inject;

import laf.test.DeploymentProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.laf.component.core.ComponentViewRepository;
import com.github.ruediste.laf.component.core.api.CView;

@RunWith(Arquillian.class)
public class ComponentViewRepositoryTest {

	@Deployment
	public static WebArchive createDeployment() {
		return DeploymentProvider.getDefault().addClasses(
				ComponentViewRepository.class, TestComponentViewA.class,
				TestComponentViewB1.class, TestComponentViewB2.class,
				CView.class);
	}

	@Inject
	ComponentViewRepository componentViewRepository;

	@Test
	public void getSimple() {
		TestControllerA controllerA = new TestControllerA();
		CView<TestControllerA> view = componentViewRepository
				.createView(controllerA);
		Assert.assertEquals(TestComponentViewA.class, view.getClass());
	}

	@Test
	public void getQualifier() {

		CView<TestControllerB> view = componentViewRepository
				.createView(new TestControllerB(), TestViewQualifier1.class);
		Assert.assertTrue(view instanceof TestComponentViewB1);
	}
}
