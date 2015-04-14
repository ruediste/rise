package com.github.ruediste.laf.core.front.reload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.ApplicationEventQueue;
import com.github.ruediste.laf.core.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.google.common.base.Preconditions;

@Singleton
public class ClassChangeNotifier {

	@Inject
	CoreConfiguration config;

	@Inject
	FileChangeNotifier notifier;

	@Inject
	ApplicationEventQueue queue;

	private Map<Path, String> classNameMap = new HashMap<>();

	public class ClassChangeTransaction {
		public Set<String> removedClasses = new HashSet<>();
		public Set<ClassNode> addedClasses = new HashSet<>();
		public Set<ClassNode> modifiedClasses = new HashSet<>();
		public boolean isInitial;
	}

	private LinkedHashSet<Consumer<ClassChangeTransaction>> preListeners = new LinkedHashSet<>();
	private LinkedHashSet<Consumer<ClassChangeTransaction>> listeners = new LinkedHashSet<>();

	private Consumer<ClassChangeTransaction> trxPostProcessor;

	private boolean isInitialized;

	/**
	 * Add a listener which will be notified before those registered with
	 * {@link #addListener(Consumer)}
	 */
	public void addPreListener(Consumer<ClassChangeTransaction> listener) {
		queue.checkAET();
		notifier.checkNotStarted();
		preListeners.add(listener);
	}

	/**
	 * Add a listener. May only be called before the underlying
	 * {@link FileChangeNotifier} has been started.
	 */
	public void addListener(Consumer<ClassChangeTransaction> listener) {
		queue.checkAET();
		notifier.checkNotStarted();
		listeners.add(listener);
	}

	public void removeListener(Consumer<ClassChangeTransaction> listener) {
		queue.checkAET();
		listeners.remove(listener);
	}

	@PostConstruct
	void setup() {
		notifier.addListener(this::changeOccurred);
	}

	public void initialize(Consumer<ClassChangeTransaction> trxPostProcessor) {
		isInitialized = true;
		this.trxPostProcessor = trxPostProcessor;
	}

	void changeOccurred(FileChangeTransaction trx) {
		Preconditions
				.checkState(isInitialized,
						"FileChangeNotifier started before initializing the ClassChangeNotifier");
		ClassChangeTransaction classTrx = new ClassChangeTransaction();
		classTrx.isInitial = trx.isInitial;

		for (Path file : trx.removedFiles) {
			if (!file.endsWith(".class")) {
				continue;
			}

			String name = classNameMap.get(file);
			if (name != null) {
				classNameMap.remove(file);
				classTrx.removedClasses.add(name);
			}
		}

		for (Path file : trx.addedFiles) {
			if (!file.getFileName().toString().endsWith(".class")) {
				continue;
			}
			ClassNode node = readClass(file);
			classNameMap.put(file, node.name);
			classTrx.addedClasses.add(node);
		}

		for (Path file : trx.modifiedFiles) {
			if (!file.getFileName().toString().endsWith(".class")) {
				continue;
			}
			classTrx.modifiedClasses.add(readClass(file));
		}

		for (Consumer<ClassChangeTransaction> listener : new ArrayList<>(
				preListeners)) {
			listener.accept(classTrx);
		}
		for (Consumer<ClassChangeTransaction> listener : new ArrayList<>(
				listeners)) {
			listener.accept(classTrx);
		}
	}

	ClassNode readClass(Path file) {
		ClassNode node = new ClassNode();
		try (InputStream in = Files.newInputStream(file)) {
			new ClassReader(in).accept(node, config.classScanningFlags);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return node;
	}
}
