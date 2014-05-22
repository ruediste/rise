package laf.component;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class ComponentUtilInitializer {

	@Inject
	ComponentUtil componentUtil;

	@PostConstruct
	public void start() {
		// access instance to make sure it is initialized
		componentUtil.initialize();
	}
}
