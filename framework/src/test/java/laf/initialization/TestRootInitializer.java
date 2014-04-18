package laf.initialization;

import javax.inject.Singleton;

@Singleton
public class TestRootInitializer {

	public boolean initialized;

	@LafInitializer
	public void initialize() {
		initialized = true;
	}
}
