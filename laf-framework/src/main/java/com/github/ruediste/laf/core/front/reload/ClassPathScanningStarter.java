package com.github.ruediste.laf.core.front.reload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker.ClassPathVisitResult;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker.ClassPathVisitor;

/**
 * Initializer to setup classpath scanning and reloading
 */
public class ClassPathScanningStarter {

	@Inject
	CoreConfiguration config;

	@Inject
	ClassChangeNotifier classChangeNotifier;

	@Inject
	FileChangeNotifier fileChangeNotifier;

	public void start() {
		HashMap<String, ClassNode> classes = new HashMap<>();
		HashSet<Path> rootDirs = new HashSet<>();

		ClassPathWalker.scan(Thread.currentThread().getContextClassLoader(),
				new ClassPathVisitor() {

					@Override
					public ClassPathVisitResult visitRootDirectory(
							Path rootDirectory, ClassLoader classloader) {
						rootDirs.add(rootDirectory);
						return ClassPathVisitResult.SKIP_CONTENTS;
					}

					@Override
					public void visitResource(String name,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						// nop
					}

					@Override
					public ClassPathVisitResult visitJarFile(Path path,
							JarFile jarFile, ClassLoader classloader) {
						return ClassPathVisitResult.CONTINUE;
					}

					@Override
					public void visitClass(String className,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						classes.computeIfAbsent(className,
								x -> readClass(inputStreamSupplier));
					}
				});

		classChangeNotifier.initialize(trx -> {
			if (trx.isInitial)
				trx.addedClasses.addAll(classes.values());
		});
		fileChangeNotifier.start(rootDirs, config.fileChangeSettleDelayMs);
	}

	protected ClassNode readClass(Supplier<InputStream> inputStreamSupplier) {
		ClassNode node = new ClassNode();
		try (InputStream in = inputStreamSupplier.get()) {
			new ClassReader(in).accept(node, config.classScanningFlags);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return node;
	}
}
