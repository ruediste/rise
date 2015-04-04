package com.github.ruediste.laf.core.classReload;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.*;

import com.github.ruediste.laf.test.SaltaTest;
import com.github.ruediste.laf.test.TestUtil;

public class DirectoryChangeWatcherTest extends SaltaTest {

	private Path tempDir;

	@Inject
	private DirectoryChangeWatcher watcher;

	private Runnable listener;

	@Before
	public void before() throws IOException {
		tempDir = Files.createTempDirectory("test");
		listener = mock(Runnable.class);

	}

	private void startWatcher() {
		watcher.start(Arrays.asList(tempDir), listener, 10);
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
		verify(listener).run();
	}

	@Test
	public void testDirAdded() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Thread.sleep(100);
		verify(listener).run();
	}

	@Test
	public void testFileAddedInSubdir() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).run();
	}

	@Test
	public void testSubdirIsRegistered() throws Exception {
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		startWatcher();
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).run();
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
		verify(listener).run();
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
		verify(listener).run();
	}

	@Test
	public void testFileAddedInSubdirSleep() throws Exception {
		startWatcher();
		Path test = tempDir.resolve("test");
		Files.createDirectory(test);
		Thread.sleep(100);
		verify(listener).run();

		reset(listener);
		Path testTxt = test.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		verify(listener).run();
	}

}
