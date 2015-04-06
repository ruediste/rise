package com.github.ruediste.laf.test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;

import com.github.ruediste.laf.core.classReload.*;
import com.github.ruediste.laf.core.entry.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public class SaltaTest {

	@Inject
	private FileChangeNotifier notifier;

	@Inject
	Scanner scanner;

	@Inject
	ApplicationEventQueue queue;

	@Before
	public void beforeSaltaTest() throws Exception {
		Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
			}
		}, new LoggerModule()).injectMembers(this);

		Set<Path> rootDirs = new HashSet<>();
		scanner.initialize((rootDirectory, classloader) -> rootDirs
				.add(rootDirectory));
		scanner.scan(Thread.currentThread().getContextClassLoader());

		queue.submit(() -> notifier.start(rootDirs, 0)).get();
	}
}
