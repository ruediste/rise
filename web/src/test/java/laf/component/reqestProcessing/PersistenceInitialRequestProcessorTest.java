package laf.component.reqestProcessing;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.component.ComponentModule;
import laf.component.reqestProcessing.PersistenceInitialRequestProcessor;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;
import laf.test.DeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceInitialRequestProcessorTest {

	@Deployment
	public static WebArchive createDeployment() {
		return DeploymentProvider.getPersistence().addClasses(
				Modules.getClasses(ComponentModule.class));
	}

	@Inject
	PersistenceInitialRequestProcessor processor;

	@Inject
	Instance<EntityManager> entityManagerInstance;

	@Test
	public void testSimple() {
		processor.initialize(new RequestProcessor() {

			@Override
			public ActionResult process(ActionPath<ParameterValueProvider> path) {
				EntityManager em = entityManagerInstance.get();
				Assert.assertTrue(em.getTransaction().isActive());
				return null;
			}
		});
	}
}
