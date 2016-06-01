package com.github.ruediste.rise.component.tree;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.component.ComponentPage;

/**
 * Interface of all components.
 * 
 * <p>
 * <b> Life Cycle </b> <br>
 * Components are directly instantiated using their constructor. This cuts them
 * of the dependency injection system, which is intended. Components are a very
 * central part of the component framework and overloading them with
 * responsibilities is an issue. Denying them access to the DI system keeps
 * focused.
 * <p>
 * There is no explicit destruction of components, as this would be a sparsely
 * used but complex feature. In particular, it is difficult to reliably detect
 * the detachment from the component tree to trigger the destruction, and
 * relying on garbage collection is not a good alternative. As alternative,
 * there is {@link ComponentPage#getDestroyEvent()} to execute code on the
 * destruction of a page.
 * <p>
 * The framework only uses weak references to components, except for the
 * representation of the component tree. Thus if a component is detached from
 * the tree and no longer referenced by the application code, the component is
 * eligible for garbage collection.
 * 
 */
public interface Component extends AttachedPropertyBearer {

}
