package com.github.ruediste.rise.nonReloadable.front;

import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.salta.jsr330.Injector;

@NonRestartable
@Singleton
public class CurrentRestartableApplicationHolder {

    public static class RestartableApplicationInfo {

        public RestartableApplication application;
        public ClassLoader reloadableClassLoader;

        public RestartableApplicationInfo(RestartableApplication application, ClassLoader reloadableClassLoader) {
            super();
            this.application = application;
            this.reloadableClassLoader = reloadableClassLoader;
        }
    }

    private volatile RestartableApplicationInfo info;

    public void clearCurrentApplication() {
        info = null;
    }

    public void setCurrentApplication(RestartableApplicationInfo info) {
        this.info = info;
    }

    public RestartableApplicationInfo info() {
        return info;
    }

    public Injector getCurrentRestartableInjector() {
        RestartableApplicationInfo info = this.info;
        if (info != null)
            return info.application.getRestartableInjector();
        else
            return null;
    }

    public ClassLoader getCurrentReloadableClassLoader() {
        RestartableApplicationInfo info = this.info;
        if (info != null)
            return info.reloadableClassLoader;
        else
            return null;
    }
}
