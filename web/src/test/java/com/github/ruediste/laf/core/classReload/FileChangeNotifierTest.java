package com.github.ruediste.laf.core.classReload;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.*;

import com.github.ruediste.laf.core.classReload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.laf.test.SaltaTest;
import com.github.ruediste.laf.test.TestUtil;

public class FileChangeNotifierTest extends SaltaTest {

	private Path tempDir;

	@Inject
	Provider<FileChangeNotifier> notifierProvider;

	@Inject
	ApplicationEventQueue queue;

	FileChangeNotifier notifier;

	private List<FileChangeTransaction> transactions;

	@Before
	public void before() throws Exception {
		notifier = notifierProvider.get();
		transactions = new ArrayList<>();
		tempDir = Files.createTempDirectory("test");
		notifier.addListener(trx -> transactions.add(trx));
	}

	private void startNotifier() throws Exception {
		queue.submit(
				() -> notifier.start(new HashSet<>(Arrays.asList(tempDir)), 10))
				.get();
	}

	@After
	public void after() throws Exception {
		TestUtil.deleteDirTree(tempDir);
	}

	@Test
	public void testFileAdded() throws Exception {
		startNotifier();
		Path testTxt = tempDir.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		assertThat(transactions, hasSize(2));
		assertThat(transactions.get(0).addedFiles, hasSize(0));
		assertThat(transactions.get(1).addedFiles, containsInAnyOrder(testTxt));
	}

	@Test
	public void testFileAddedAndRemoved() throws Exception {
		startNotifier();
		Path testTxt = tempDir.resolve("test.txt");
		Files.write(testTxt, "Hello".getBytes());
		Thread.sleep(100);
		transactions.clear();
		Files.delete(testTxt);
		Thread.sleep(100);
		assertThat(transactions, hasSize(1));
		assertThat(transactions.get(0).removedFiles,
				containsInAnyOrder(testTxt));
	}

	@Test
	public void testFileModified() throws Exception {
		startNotifier();
		Path testTxt1 = tempDir.resolve("test1.txt");
		Files.write(testTxt1, "Hello".getBytes());
		Path testTxt2 = tempDir.resolve("test2.txt");
		Files.write(testTxt2, "Hello".getBytes());

		// let at least one second pass, such that the mtime changes
		// Thread.sleep(1500);
		Thread.sleep(100);

		transactions.clear();
		Files.write(testTxt2, "Hello World".getBytes());
		Thread.sleep(100);
		assertThat(transactions, hasSize(1));
		assertThat(transactions.get(0).modifiedFiles,
				containsInAnyOrder(testTxt2));
	}
}
