package laf.skeleton.base;

import javax.inject.Inject;

import laf.integration.IntegrationUtil;
import laf.mvc.web.MWRenderUtil;
import laf.mvc.web.MvcWebView;

public abstract class MvcViewBase<TData> extends MvcWebView<TData> {
	@Inject
	protected IntegrationUtil iUtil;

	@Inject
	protected MWRenderUtil util;

	public void setRenderUtil(MWRenderUtil util) {
		this.util = util;
	}

	public void setIntegrationUtil(IntegrationUtil iUtil) {
		this.iUtil = iUtil;
	}
}
