package com.github.ruediste.rise.core;

import javax.inject.Inject;

import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathScanningStarter;
import com.github.ruediste.rise.util.Initializer;
import com.google.common.base.Stopwatch;

public class CoreNonRestartableInitializer implements Initializer {
    @Inject
    ClassPathScanningStarter classPathScanningStarter;

    @Override
    public void initialize() {
        // start the file change/ class change notifier
        Stopwatch watch = Stopwatch.createStarted();
        classPathScanningStarter.start();
        StartupTimeLogger.stopAndLog("Classpath Scanning", watch);
    }

}
