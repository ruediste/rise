package laf.core.defaultConfiguration;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;

import ro.isdc.wro.extensions.processor.support.sass.RubySassEngine;

/**
 * Singleton loaded at startup which starts the jRuby initialization in a
 * separate thread. Used to shorten startup time.
 */
@javax.ejb.Singleton
@Startup
public class RubySassPreloader {

	@PostConstruct
	public void initialize() {
		new Thread(() -> {
			new RubySassEngine().process("foo{}");
		}).start();
	}

}
