package laf.initializer;

import laf.LAF;

/**
 * The pupose of the initializer system is to organize the initialization of the
 * framework components. Since the initialization of these components do often
 * depend on each other, it is not sufficient to just invoke the initializers in
 * any order, for example using an event.
 *
 * <p>
 * Instead each component can define one or more {@link Initializer}s. Each
 * initializer defines upon which other initializers it depends, and which
 * initializers have to be run afterwards. This dependency graph is then checked
 * for the absence of cycles (it has to be a DAG) and sorted by the dependency
 * relation. Then the initializers are run. This is implemented by the
 * {@link InitializationEngine}.
 * </p>
 *
 * <p>
 * Each element configured in {@link LAF} is checked for initializer
 * definitions. Initializers can be defined by implementing the
 * {@link InitializerProvider} interface, or by declaring a method annotated
 * with {@link LafInitializer}.
 * </p>
 */
public class InitializerConcepts {

}
