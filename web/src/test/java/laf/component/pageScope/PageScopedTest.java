package laf.component.pageScope;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.PostRemove;

import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PageScopedTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive result = DeploymentProvider.getDefault().addClasses(
				Modules.getAllRequiredClasses(PageScopeModule.class));
		System.out.println(result.toString(true));
		return result;
	}

	public static class TestBeanA implements Serializable {
		private static final long serialVersionUID = 1L;
		@Inject
		TestBeanB beanB;
	}

	@PageScoped
	public static class TestBeanB implements Serializable {
		private static final long serialVersionUID = 1L;
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@PostRemove
		public void removed() {

		}
	}

	@Inject
	TestBeanA beanA1;

	@Inject
	TestBeanA beanA2;

	@Inject
	PageScopeManager manager;

	@Test
	public void simple() {
		long id1 = manager.enterNew();
		beanA1.beanB.setValue("Hello");
		Assert.assertEquals("Hello", beanA1.beanB.getValue());
		manager.leave();

		long id2 = manager.enterNew();
		beanA1.beanB.setValue("World");
		Assert.assertEquals("World", beanA1.beanB.getValue());
		manager.leave();

		manager.enter(id1);
		Assert.assertEquals("Hello", beanA1.beanB.getValue());
		manager.leave();
	}
}
