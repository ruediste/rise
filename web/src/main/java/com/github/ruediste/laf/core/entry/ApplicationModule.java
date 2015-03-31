package com.github.ruediste.laf.core.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.util.LoggerCreationRule;

public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
		bindCreationRule(new LoggerCreationRule(Logger.class,
				LoggerFactory::getLogger));
	}

}