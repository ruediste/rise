package laf.component.core.tree;

import laf.component.core.binding.BindingModule;

import org.jabsaw.Module;

/**
 * Base classes for the Component Tree.
 *
 * When a view is initialized, it constructs a component tree which represents
 * the UI.
 *
 * All components must implement the {@link Component} interface. It provides
 * the functionality to navigate the tree (children and parents) and methods to
 * notify a component that children have been removed or that the parent changed
 * but does not provide methods to initiate a change to the tree.
 *
 * Providing methods for the modification of the tree structure are the
 * responsibility of the component interface implementations.
 *
 * The {@link ComponentBase} class introduces the concept of
 * {@link ChildRelation}s. Components do not directly contain lists or sets of
 * their children but contain one or more child relations, which can be public.
 * All the child adding and removing logic is contained in the relation classes.
 * This significantly reduces the amount of boilerplate code necessary to
 * implement child relations and provides a consistent interface.
 *
 * Finally, the {@link ComponentTreeUtil} class provides utility methods for
 * tree navigation.
 */
@Module(description = "Base Classes for the Component Tree", exported = {
		laf.core.base.attachedProperties.CoreAttachedPropertiesModule.class,
		BindingModule.class })
public class ComponentTreeModule {

}
