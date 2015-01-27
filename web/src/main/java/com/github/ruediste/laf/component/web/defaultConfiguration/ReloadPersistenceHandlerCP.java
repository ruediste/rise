package com.github.ruediste.laf.component.web.defaultConfiguration;

import com.github.ruediste.laf.component.core.DelegatingRequestHandler;
import com.github.ruediste.laf.component.core.PageReloadRequest;
import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;

public interface ReloadPersistenceHandlerCP
		extends
		ConfigurationParameter<DelegatingRequestHandler<PageReloadRequest, PageReloadRequest>> {

}
