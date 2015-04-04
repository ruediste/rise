package com.github.ruediste.laf.test;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.jsr330.util.LoggerCreationRule;

public class SaltaTest {

	@Before
	public void beforeSaltaTest() {
		Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
				bindCreationRule(new LoggerCreationRule(Logger.class,
						cls -> LoggerFactory.getLogger(cls)));
			}
		}).injectMembers(this);
	}
}
