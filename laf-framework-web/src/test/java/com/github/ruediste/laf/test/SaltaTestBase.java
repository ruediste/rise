package com.github.ruediste.laf.test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;

import com.github.ruediste.laf.core.front.ApplicationEventQueue;
import com.github.ruediste.laf.core.front.LoggerModule;
import com.github.ruediste.laf.core.front.reload.FileChangeNotifier;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public abstract class SaltaTestBase {

	@Inject
	private FileChangeNotifier notifier;

	@Inject
	ApplicationEventQueue queue;

	@Inject
	private ClassPathWalker scanner;

	@Before
	public void beforeSaltaTest() throws Exception {
		Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
			}
		}, new LoggerModule()).injectMembers(this);

		queue.submit(this::startInAET).get();
	}

	protected void initialize() {
	}

	private void startInAET() {
		initialize();
		Set<Path> rootDirs = new HashSet<>();
		scanner.initialize((rootDirectory, classloader) -> rootDirs
				.add(rootDirectory));
		scanner.scan(Thread.currentThread().getContextClassLoader());
		notifier.start(rootDirs, 10);
	}
}
