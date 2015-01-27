package com.github.ruediste.laf.component.core;

import javax.enterprise.context.ApplicationScoped;

import org.jabsaw.Module;

import com.github.ruediste.laf.component.core.pageScope.PageScopeModule;
import com.github.ruediste.laf.component.core.tree.Component;
import com.github.ruediste.laf.component.core.tree.ComponentTreeModule;
import com.github.ruediste.laf.component.core.tree.event.ComponentEventModule;
import com.github.ruediste.laf.core.CoreModule;

/**
 *
 * <strong> View Construction </strong> <br/>
 * The views do not need no access to injected resources. The few required
 * services are made available through static utility functions. Thus they can
 * be instantiated using the new operator. After instantiation, the controller
 * can configure the view. When a page is rendered for the first time, the
 * {@link Component#initialize()} method is called
 *
 * <strong> Nesting Views </strong> <br/>
 * A controller A can have sub controllers. This relationship is not modeled by
 * the framework. After building the component tree, the view of controller A,
 * VA, has to have included the views of the sub controllers in it's component
 * tree. The following approaches are possible:
 * <dl>
 * <dt>VA explicitly adds the sub views</dt>
 * <dd>During component tree construction, VA retrieves the sub controllers from
 * controller A and adds their views to a containing {@link Component}</dd>
 * <dt>Controller A adds the sub views to VA</dt>
 * <dd>After creating VA, the controller A retrieves the views of the sub
 * controllers and adds them to VA</dd>
 * <dt></dt>
 * <dd></dd>
 * <dt></dt>
 * <dd></dd>
 * </dl>
 * Views can be nested into each other.
 */
@Module(description = "Core of the Component Framework", exported = {
		CoreModule.class, ComponentTreeModule.class, PageScopeModule.class,
		ComponentEventModule.class, ComponentEventModule.class,
		com.github.ruediste.laf.component.core.api.ComponentCoreApiModule.class })
@ApplicationScoped
public class ComponentCoreModule {

}
