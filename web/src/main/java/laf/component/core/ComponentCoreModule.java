package laf.component.core;

import javax.enterprise.context.ApplicationScoped;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.component.pageScope.PageScopeModule;
import laf.component.tree.Component;
import laf.component.tree.ComponentTreeModule;
import laf.component.tree.event.ComponentEventModule;

import org.jabsaw.Module;

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
		ComponentTreeModule.class, BaseModule.class, PageScopeModule.class,
		ComponentEventModule.class, ComponentEventModule.class,
		ActionPathModule.class })
@ApplicationScoped
public class ComponentCoreModule {

}
