package com.github.ruediste.laf.component.core.pageScope;

import org.jabsaw.Module;

/**
 * The {@link PageScoped} is a CDI scope used to manage instances associated
 * with a page. The {@link PageScopeManager} manages the active scope and allows
 * control of which page scope is active {@link PageScopeManager#enter(long)}
 */
@Module(description = "Provides the PageScope scope")
public class PageScopeModule {

}
