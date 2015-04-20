package com.github.ruediste.laf.core.front.reload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker.ClassPathVisitResult;
import com.github.ruediste.laf.core.front.reload.ClassPathWalker.ClassPathVisitor;
import com.google.common.io.ByteStreams;

/**
 * Initializer to setup classpath scanning and reloading
 */
public class ClassPathScanningStarter {

	@Inject
	Logger log;

	@Inject
	CoreConfiguration config;

	@Inject
	ClassChangeNotifier classChangeNotifier;

	@Inject
	FileChangeNotifier fileChangeNotifier;

	public void start() {
		HashMap<String, ClassNode> classes = new HashMap<>();
		HashSet<Path> rootDirs = new HashSet<>();

		ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
				new CallerRunsPolicy());

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
						if (classes.containsKey(className))
							return;

						byte[] bb = null;
						try (InputStream in = inputStreamSupplier.get()) {
							bb = ByteStreams.toByteArray(in);
						} catch (IOException e) {
							log.error("Unable to read class " + className, e);
						}
						if (bb != null) {
							final byte[] b = bb;
							executor.execute(() -> {
								if (classes.containsKey(className))
									return;
								ClassNode cls = readClass(b);
								classes.putIfAbsent(className, cls);
							});
						}

					}
				});

		classChangeNotifier.initialize(trx -> {
			if (trx.isInitial)
				trx.addedClasses.addAll(classes.values());
		});
		fileChangeNotifier.start(rootDirs, config.fileChangeSettleDelayMs);
	}

	protected ClassNode readClass(byte[] bb) {
		ClassNode node = new ClassNode();
		new ClassReader(bb).accept(node, config.classScanningFlags);
		return node;
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
