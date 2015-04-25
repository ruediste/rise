package com.github.ruediste.laf.integrationTest;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;

import com.github.ruediste.laf.core.entry.StandaloneLafApplication;

/**
 * Fires up a server and provides access to the included
 */
public class IntegrationTestBase {

	public static AtomicBoolean started = new AtomicBoolean(false);

	@Before
	public void beforeIntegrationTestBase() {
		if (started.getAndSet(true))
			return;

		new StandaloneLafApplication().startForTesting(null, 0);
	}
}
