package laf.component;

import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;
import laf.test.ComponentDeploymentProvider;

import org.jabsaw.util.Modules;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceInitialRequestProcessorTest {

	@Deployment
	public static WebArchive createDeployment() {
		return ComponentDeploymentProvider.getPersistence().addClasses(
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
				assertTrue(em.getTransaction().isActive());
				return null;
			}
		});
	}
}
