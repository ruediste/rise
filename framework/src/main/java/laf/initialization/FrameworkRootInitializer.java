package laf.initialization;

import javax.inject.Singleton;

@Singleton
public class FrameworkRootInitializer {

	private boolean initialized;

	@LafInitializer
	public void initialize() {
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}
}
