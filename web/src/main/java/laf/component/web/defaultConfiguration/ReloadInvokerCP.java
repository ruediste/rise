package laf.component.web.defaultConfiguration;

import laf.component.core.RequestHandler;
import laf.component.core.reqestProcessing.PageReloadRequest;
import laf.core.base.configuration.ConfigurationParameter;

public interface ReloadInvokerCP extends
		ConfigurationParameter<RequestHandler<PageReloadRequest>> {

}
