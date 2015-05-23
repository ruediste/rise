package com.github.ruediste.rise.core;

import javax.inject.Inject;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathScanningStarter;
import com.github.ruediste.rise.util.Initializer;

public class CoreNonRestartableInitializer implements Initializer {
    @Inject
    ClassPathScanningStarter classPathScanningStarter;

    @Override
    public void initialize() {
        // start the file change/ class change notifier
        classPathScanningStarter.start();
    }

}
