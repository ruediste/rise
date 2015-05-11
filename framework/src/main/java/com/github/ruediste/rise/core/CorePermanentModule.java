package com.github.ruediste.rise.core;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadableClassLoader;
import com.github.ruediste.rise.nonReloadable.front.reload.ReloadebleClassesIndex;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class CorePermanentModule extends AbstractModule {

	private ServletConfig servletConfig;

	public CorePermanentModule(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	@Override
	protected void configure() throws Exception {
		bind(ClassHierarchyCache.class).asEagerSingleton();
		InitializerUtil.register(config(), CorePermanentInitializer.class);
		bind(FileChangeNotifier.class).named("classPath").in(Singleton.class);
	}

	@Named("dynamic")
	@Provides
	ReloadableClassLoader spaceAwareClassLoaderDynamic(ReloadebleClassesIndex cache) {
		return new ReloadableClassLoader(Thread.currentThread()
				.getContextClassLoader(), cache);
	}

	@Provides
	ServletConfig servletConfig() {
		return servletConfig;
	}

}