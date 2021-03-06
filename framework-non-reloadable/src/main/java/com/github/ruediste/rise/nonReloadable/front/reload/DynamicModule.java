package com.github.ruediste.rise.nonReloadable.front.reload;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Base class for dynamic salta modules.
 * 
 * @see PermanentModule PermanentModule for details
 */
public abstract class DynamicModule extends AbstractModule {

    protected final Injector permanentInjector;

    public DynamicModule(Injector permanentInjector) {
        this.permanentInjector = permanentInjector;
    }

    protected void bindToPermanentInjector(Class<ClassHierarchyIndex> clazz) {
        bind(clazz).toProvider(() -> permanentInjector.getInstance(clazz));
    }

}