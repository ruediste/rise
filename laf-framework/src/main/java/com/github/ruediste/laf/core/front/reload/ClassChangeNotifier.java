package com.github.ruediste.laf.core.front.reload;

import static java.util.stream.Collectors.joining;

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
import javax.inject.Named;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.ApplicationEventQueue;
import com.github.ruediste.laf.core.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.laf.util.Pair;
import com.google.common.base.Preconditions;

@Singleton
public class ClassChangeNotifier {

	@Inject
	Logger log;

	@Inject
	CoreConfiguration config;

	@Inject
	@Named("classPath")
	FileChangeNotifier notifier;

	@Inject
	ApplicationEventQueue queue;

	private Map<Path, String> classNameMap = new HashMap<>();

	public static class ClassChangeTransaction {
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
		notifier.checkNotStarted();
		preListeners.add(listener);
	}

	/**
	 * Add a listener. May only be called before the underlying
	 * {@link FileChangeNotifier} has been started.
	 */
	public void addListener(Consumer<ClassChangeTransaction> listener) {
		notifier.checkNotStarted();
		listeners.add(listener);
	}

	public void removeListener(Consumer<ClassChangeTransaction> listener) {
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

		trx.addedFiles
				.stream()
				.parallel()
				.filter(file -> file.getFileName().toString()
						.endsWith(".class"))
				.map(file -> Pair.of(file, readClass(file))).sequential()
				.forEach(pair -> {
					classNameMap.put(pair.getA(), pair.getB().name);
					classTrx.addedClasses.add(pair.getB());

				});
		// for (Path file : trx.addedFiles) {
		// if (!file.getFileName().toString().endsWith(".class")) {
		// continue;
		// }
		// ClassNode node = readClass(file);
		// classNameMap.put(file, node.name);
		// classTrx.addedClasses.add(node);
		// }

		for (Path file : trx.modifiedFiles) {
			if (!file.getFileName().toString().endsWith(".class")) {
				continue;
			}
			classTrx.modifiedClasses.add(readClass(file));
		}

		trxPostProcessor.accept(classTrx);

		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Publishing Class Change Transaction: \n");
			sb.append("Added:\n");
			sb.append(classTrx.addedClasses.stream().map(n -> n.name)
					.collect(joining("\n", "  ", "")));
			sb.append("Removed:\n");
			sb.append(classTrx.removedClasses.stream().collect(
					joining("\n", "  ", "")));
			sb.append("Modified:\n");
			sb.append(classTrx.modifiedClasses.stream().map(n -> n.name)
					.collect(joining("\n", "  ", "")));
			log.trace(sb.toString());
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
