package laf.initialization;

import javax.inject.Singleton;

@Singleton
public class TestInitializer {

	public boolean initialized;

	@LafInitializer(before = TestRootInitializer.class)
	public void initialize() {
		initialized = true;
	}
}
