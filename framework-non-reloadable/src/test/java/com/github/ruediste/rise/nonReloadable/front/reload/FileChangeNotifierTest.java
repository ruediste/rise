package com.github.ruediste.rise.nonReloadable.front.reload;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.nonReloadable.front.ApplicationEventQueue;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.rise.nonReloadable.front.reload.DirectoryChangeWatcher;
import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier;
import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.rise.test.TestUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public class FileChangeNotifierTest {

    private Path tempDir;

    @Inject
    ApplicationEventQueue queue;

    @Inject
    @Named("classPath")
    FileChangeNotifier notifier;

    @Inject
    DirectoryChangeWatcher watcher;

    private List<FileChangeTransaction> transactions;

    @Before
    public void before() throws Exception {
        Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                bind(FileChangeNotifier.class).named("classPath").in(
                        Singleton.class);
            }

        }, new LoggerModule()).injectMembers(this);

        transactions = new ArrayList<>();
        notifier.addListener(trx -> transactions.add(trx));
        tempDir = Files.createTempDirectory("test");
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
        assertThat(transactions.get(1).addedFiles, contains(testTxt));
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
        assertThat(transactions.get(0).removedFiles, contains(testTxt));
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
        assertThat(transactions.get(0).modifiedFiles, contains(testTxt2));
    }
}
