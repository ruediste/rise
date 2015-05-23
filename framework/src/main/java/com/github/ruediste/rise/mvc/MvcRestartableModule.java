package com.github.ruediste.rise.mvc;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.DynamicModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcRestartableModule extends DynamicModule {

    public MvcRestartableModule(Injector permanentInjector) {
        super(permanentInjector);
    }

    @Override
    protected void configure() throws Exception {
        bindToPermanentInjector(ClassHierarchyIndex.class);
        InitializerUtil.register(config(), MvcDynamicInitializer.class);
    }
}
