package com.github.ruediste.rise.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import com.github.ruediste.remoteJUnit.codeRunner.CodeRunnerCommon.Response;
import com.github.ruediste.remoteJUnit.codeRunner.CodeRunnerRequestHandler;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.util.Initializer;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

public class RemotUnitTestInitializer implements Initializer {

    @Inject
    CoreConfiguration config;

    @Inject
    PathInfoIndex index;

    @Inject
    CoreRequestInfo info;

    @Inject
    Injector injector;

    @NonRestartable
    @Inject
    Injector nonRestartableInjector;

    @Override
    public void initialize() {
        if (!Strings.isNullOrEmpty(config.unitTestCodeRunnerPathInfo)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            CodeRunnerRequestHandler handler = new CodeRunnerRequestHandler(
                    config.dynamicClassLoader, r -> pool.execute(() -> {
                        Thread thread = Thread.currentThread();
                        ClassLoader oldCl = thread.getContextClassLoader();
                        thread.setContextClassLoader(config.dynamicClassLoader);
                        try {
                            InjectorsHolder.withInjectors(
                                    nonRestartableInjector, injector, r);
                        } finally {
                            thread.setContextClassLoader(oldCl);

                        }
                    }));
            index.registerPathInfo(config.unitTestCodeRunnerPathInfo,
                    request -> new RequestParseResult() {

                        @Override
                        public void handle() {
                            try {
                                Response response = handler.handle(info
                                        .getServletRequest().getInputStream());
                                try (OutputStream os = info.getServletResponse()
                                        .getOutputStream()) {
                                    ByteStreams.copy(new ByteArrayInputStream(
                                            CodeRunnerRequestHandler
                                                    .toByteArray(response)),
                                            os);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public String toString() {
                            return "UnitTestParseResult";
                        }
                    });
        }

    }
}
