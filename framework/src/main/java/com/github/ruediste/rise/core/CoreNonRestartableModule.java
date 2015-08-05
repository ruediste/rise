package com.github.ruediste.rise.core;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassLoader;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassesIndex;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class CoreNonRestartableModule extends AbstractModule {

    private ServletConfig servletConfig;

    public CoreNonRestartableModule(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    protected void configure() throws Exception {
        InitializerUtil.register(config(), CoreNonRestartableInitializer.class);
        bind(FileChangeNotifier.class).named("classPath").in(Singleton.class);
        bind(ReloadableClassesIndex.class).asEagerSingleton();
    }

    @Named("dynamic")
    @Provides
    ReloadableClassLoader spaceAwareClassLoaderDynamic(
            ReloadableClassesIndex cache) {
        return new ReloadableClassLoader(Thread.currentThread()
                .getContextClassLoader(), cache);
    }

    @Provides
    ServletConfig servletConfig() {
        return servletConfig;
    }

}