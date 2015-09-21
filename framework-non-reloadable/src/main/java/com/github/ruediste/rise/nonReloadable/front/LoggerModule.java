package com.github.ruediste.rise.nonReloadable.front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.util.LoggerCreationRule;

public class LoggerModule extends AbstractModule {

    @Override
    protected void configure() {
        bindCreationRule(
                new LoggerCreationRule(Logger.class, LoggerFactory::getLogger));
        bindCreationRule(new LoggerCreationRule(java.util.logging.Logger.class,
                cls -> java.util.logging.Logger.getLogger(cls.getName())));
    }

}