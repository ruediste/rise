package com.github.ruediste.laf.component.core.pageScope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

public class PageScopeExtension implements Extension {

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery e) {
		e.addScope(PageScoped.class, true, true);
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery e,
			BeanManager manager) {
		e.addContext(new PageContext());
	}
}
