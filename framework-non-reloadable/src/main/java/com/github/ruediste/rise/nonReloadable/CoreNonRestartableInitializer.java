package com.github.ruediste.rise.nonReloadable;

import javax.inject.Inject;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathScanningStarter;
import com.github.ruediste.rise.nonReloadable.front.reload.MemberOrderIndex;
import com.github.ruediste.rise.util.Initializer;

public class CoreNonRestartableInitializer implements Initializer {
    @Inject
    ClassPathScanningStarter classPathScanningStarter;

    @Inject
    ClassHierarchyIndex classHierarchyIndex;

    @Inject
    MemberOrderIndex memberOrderIndex;

    @Inject
    CoreConfigurationNonRestartable config;

    @Override
    public void initialize() {
        config.initialize();

        classHierarchyIndex.setup();
        memberOrderIndex.setup();

        // start the file change/ class change notifier
        classPathScanningStarter.start();
    }

}
