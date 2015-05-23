package com.github.ruediste.rise.mvc;

import javax.inject.Inject;

import com.github.ruediste.rise.util.Initializer;

public class MvcDynamicInitializer implements Initializer {

    @Inject
    MvcConfiguration config;

    @Override
    public void initialize() {
        config.initialize();
    }

}
