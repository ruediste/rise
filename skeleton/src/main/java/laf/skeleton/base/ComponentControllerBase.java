package laf.skeleton.base;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.component.core.api.CController;
import laf.component.web.CWControllerUtil;
import laf.integration.IntegrationUtil;

@CController
public class ComponentControllerBase {

	@Inject
	protected CWControllerUtil util;

	@Inject
	protected IntegrationUtil iUtil;

	@Inject
	protected EntityManager em;

	public void setControllerUtil(CWControllerUtil util) {
		this.util = util;
	}

	public void setIntegrationUtil(IntegrationUtil iUtil) {
		this.iUtil = iUtil;
	}

	public void setEntityManager(EntityManager entityManager) {
		em = entityManager;
	}
}
