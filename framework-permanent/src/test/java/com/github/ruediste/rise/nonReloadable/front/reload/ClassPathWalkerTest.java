package com.github.ruediste.rise.nonReloadable.front.reload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.ClassPathVisitResult;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassPathWalker.SimpleClassPathVisitor;

public class ClassPathWalkerTest {

	boolean found;

	@Before
	public void before() {
		found = false;
	}

	@Test
	public void jarClassFound() {
		ClassPathWalker.scan(ClassPathWalkerTest.class.getClassLoader(),
				new SimpleClassPathVisitor() {
					@Override
					public void visitClass(String className,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						if (Inject.class.getName().equals(className))
							found = true;
					}
				});
		assertTrue(found);
	}

	@Test
	public void skipJar() throws IOException {
		ClassPathWalker.scan(getClass().getClassLoader(),
				new SimpleClassPathVisitor() {

					@Override
					public ClassPathVisitResult visitJarFile(Path path,
							JarFile jarFile, ClassLoader classloader) {
						if (path.getFileName().toString()
								.startsWith("javax.inject")) {
							return ClassPathVisitResult.SKIP_CONTENTS;
						} else
							return ClassPathVisitResult.CONTINUE;
					}

					@Override
					public void visitClass(String className,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						if (Inject.class.getName().equals(className))
							found = true;
					}
				});
		assertFalse(found);
	}

	@Test
	public void dirClassFound() {
		ClassPathWalker.scan(ClassPathWalkerTest.class.getClassLoader(),
				new SimpleClassPathVisitor() {
					@Override
					public void visitClass(String className,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						if (ClassPathWalker.class.getName().equals(className))
							found = true;
					}
				});
		assertTrue(found);
	}

	@Test
	public void skipDir() {
		ClassPathWalker.scan(ClassPathWalkerTest.class.getClassLoader(),
				new SimpleClassPathVisitor() {

					@Override
					public ClassPathVisitResult visitRootDirectory(
							Path rootDirectory, ClassLoader classloader) {
						if (rootDirectory.toString().contains(
								"rise/framework-permanent/")) {
							return ClassPathVisitResult.SKIP_CONTENTS;
						} else
							return ClassPathVisitResult.CONTINUE;
					}

					@Override
					public void visitClass(String className,
							ClassLoader classLoader,
							Supplier<InputStream> inputStreamSupplier) {
						if (ClassPathWalker.class.getName().equals(className))
							found = true;
					}
				});
		assertFalse(found);
	}
}
