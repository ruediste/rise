package laf.skeleton.base;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.api.CView;
import com.github.ruediste.laf.component.web.CWRenderUtil;
import com.github.ruediste.laf.integration.IntegrationUtil;

public abstract class ComponentViewBase<TController> extends CView<TController> {

	@Inject
	protected CWRenderUtil util;

	@Inject
	protected IntegrationUtil iUtil;

	public void setRenderUtil(CWRenderUtil util) {
		this.util = util;
	}

	public void setIntegrationUtil(IntegrationUtil iUtil) {
		this.iUtil = iUtil;
	}
}
