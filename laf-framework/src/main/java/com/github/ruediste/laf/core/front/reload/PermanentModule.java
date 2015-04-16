package com.github.ruediste.laf.core.front.reload;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Base class for permanent Salta modules.
 * 
 * <p>
 * When the application starts, an {@link Injector} is created using the
 * permanent modules. Afterwards, another injector is created using the
 * {@link DynamicModule}s. This injector is recreated whenever the dynamic part
 * of the appication is reloaded
 * </p>
 */
public abstract class PermanentModule extends AbstractModule {

}
