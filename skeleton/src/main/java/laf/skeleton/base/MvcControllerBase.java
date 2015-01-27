package laf.skeleton.base;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.laf.integration.IntegrationUtil;
import com.github.ruediste.laf.mvc.core.api.MController;
import com.github.ruediste.laf.mvc.web.MWControllerUtil;

@MController
public abstract class MvcControllerBase {

	@Inject
	protected MWControllerUtil util;

	@Inject
	protected IntegrationUtil iUtil;

	@Inject
	protected EntityManager em;

	public void setControllerUtil(MWControllerUtil util) {
		this.util = util;
	}

	public void setIntegrationUtil(IntegrationUtil iUtil) {
		this.iUtil = iUtil;
	}

	public void setEntityManager(EntityManager entityManager) {
		em = entityManager;
	}
}
