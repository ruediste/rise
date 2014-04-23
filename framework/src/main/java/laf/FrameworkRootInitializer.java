package laf;

import javax.inject.Singleton;

import laf.initialization.LafInitializer;

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
