package com.github.ruediste.rise.nonReloadable;

import javax.inject.Named;
import javax.servlet.ServletConfig;

import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.front.reload.ClasspathResourceIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassLoader;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassesIndex;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class CoreNonRestartableModule extends AbstractModule {

    private FrontServletBase frontServlet;

    public CoreNonRestartableModule(FrontServletBase frontServlet) {
        this.frontServlet = frontServlet;
    }

    @Override
    protected void configure() throws Exception {
        InitializerUtil.register(config(), CoreNonRestartableInitializer.class);
        bind(ReloadableClassesIndex.class).asEagerSingleton();
        bind(ClasspathResourceIndex.class).asEagerSingleton();
    }

    @Named("dynamic")
    @Provides
    ReloadableClassLoader spaceAwareClassLoaderDynamic(ReloadableClassesIndex cache) {
        return new ReloadableClassLoader(Thread.currentThread().getContextClassLoader(), cache);
    }

    @Provides
    FrontServletBase frontServlet() {
        return frontServlet;
    }

    @Provides
    ServletConfig servletConfig() {
        return frontServlet.getServletConfig();
    }

}