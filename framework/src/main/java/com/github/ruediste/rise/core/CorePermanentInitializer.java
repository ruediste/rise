package com.github.ruediste.rise.core;

import javax.inject.Inject;

import com.github.ruediste.rise.core.front.reload.ClassPathScanningStarter;
import com.github.ruediste.rise.util.Initializer;

public class CorePermanentInitializer implements Initializer {
	@Inject
	ClassPathScanningStarter classPathScanningStarter;

	@Override
	public void initialize() {
		// start the file change/ class change notifier
		classPathScanningStarter.start();
	}

}
