package com.github.ruediste.rise.core;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import com.github.ruediste.rise.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.rise.core.front.reload.ClassSpaceCache;
import com.github.ruediste.rise.core.front.reload.DynamicSpace;
import com.github.ruediste.rise.core.front.reload.FileChangeNotifier;
import com.github.ruediste.rise.core.front.reload.PermanentSpace;
import com.github.ruediste.rise.core.front.reload.SpaceAwareClassLoader;
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
	SpaceAwareClassLoader spaceAwareClassLoaderDynamic(ClassSpaceCache cache) {
		return new SpaceAwareClassLoader(Thread.currentThread()
				.getContextClassLoader(), DynamicSpace.class, cache,
				PermanentSpace.class);
	}

	@Provides
	ServletConfig servletConfig() {
		return servletConfig;
	}

}