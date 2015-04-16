package com.github.ruediste.laf.core;

import javax.inject.Inject;

import com.github.ruediste.laf.core.front.reload.ClassPathScanningStarter;
import com.github.ruediste.laf.util.Initializer;

public class CoreApplicationInitializer implements Initializer {
	@Inject
	ClassPathScanningStarter classPathScanningStarter;

	@Override
	public void initialize() {
		// start the file change/ class change notifier
		classPathScanningStarter.start();
	}

}
