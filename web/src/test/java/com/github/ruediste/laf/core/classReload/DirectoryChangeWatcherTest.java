package com.github.ruediste.laf.core.classReload;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.laf.test.SaltaTest;
import com.github.ruediste.laf.test.TestUtil;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DirectoryChangeWatcherTest extends SaltaTest {

	private Path tempDir;

	@Inject
	ApplicationEventQueue queue;

	@Inject
	private DirectoryChangeWatcher watcher;

	@Mock
	private Consumer<Set<Path>> listener;

	@Before
	public void before() throws IOException {
		tempDir = Files.createTempDirectory("test");

	}

	private void startWatcher() throws Exception {
		queue.submit(() -> watcher.start(Arrays.asList(tempDir), listener, 10))
				.get();
	}

	@After
	public void after() throws IOException {
		TestUtil.deleteDirTree(tempDir);
	}

	@Test
	public void testFileAdded() throws Exception {

		startWatcher();
		Path testTxt = tempDir.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).accept(
				(Set<Path>) argThat(containsInAnyOrder(testTxt)));
	}

	@Test
	public void testDirAdded() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Thread.sleep(100);
		verify(listener).accept((Set<Path>) argThat(containsInAnyOrder(test)));
	}

	@Test
	public void testFileAddedInSubdir() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).accept((Set<Path>) argThat(containsInAnyOrder(test)));
	}

	@Test
	public void testSubdirIsRegistered() throws Exception {
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		startWatcher();
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).accept(
				(Set<Path>) argThat(containsInAnyOrder(testTxt)));
	}

	@Test
	public void testRemoval() throws Exception {
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		startWatcher();
		Files.delete(testTxt);
		Thread.sleep(100);
		verify(listener).accept(
				(Set<Path>) argThat(containsInAnyOrder(testTxt)));
	}

	@Test
	public void testModification() throws Exception {
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		startWatcher();
		Files.write(testTxt, "Hello World".getBytes());
		Thread.sleep(100);
		verify(listener).accept(
				(Set<Path>) argThat(containsInAnyOrder(testTxt)));
	}

	@Test
	public void testFileAddedInSubdirSleep() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Thread.sleep(100);
		verify(listener).accept((Set<Path>) argThat(containsInAnyOrder(test)));

		reset(listener);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).accept(
				(Set<Path>) argThat(containsInAnyOrder(testTxt)));
	}

}
