package laf.initialization;

import laf.FrameworkRootInitializer;

import org.jabsaw.Module;

/**
 * The pupose of the initialization system is to manage the initialization of
 * the framework.
 *
 * <p>
 * The initialization process is carried out by {@link Initializer}s. Since
 * initializers do often depend on each other, it is not sufficient to just
 * invoke the initializers in any order, for example using an event. Instead, a
 * dependency graph of the initializers is defined. The initializers are
 * represented by the vertices, the edges represent the depends relation, with
 * the source depending on the target. The relation between two initializers can
 * be defined by both the source or the target initializer. This allows the
 * initializers of a framework extension to define initializers, without the
 * initializers of the framework beeing aware of the extension.
 * </p>
 *
 * <p>
 * A dependency can be optional or mandatory. When initializing the framework,
 * the dependency graph is traversed starting with the
 * {@link FrameworkRootInitializer}, taking only mandatory dependencies into
 * account. The reachable initializers are sorted by both mandatory and optional
 * dependencies and executed in order. Thus, a mandatory dependency guarantees
 * that the target initializer is always executed before the source initializer.
 * An optional dependency only guarantees that if the target initializer is
 * executed, it is executed before the source initializer. But the target
 * initializer may not be executed at all.
 * </p>
 *
 * <p>
 * An initializer is identified by a representing class. While it is perfectly
 * possible to use multiple initializers with the same representing class, the
 * dependency relation is often defined in terms of the representing class and
 * not directly by the initializer instance. In this case, all initializers with
 * the same representing class are treated equal. It is generally best to avoid
 * using multiple initializers with the same representing class.
 * </p>
 *
 * <p>
 * The core of the initialization system is based on {@link Initializer}
 * instances. The method
 * {@link InitializationService#runInitializers(Initializer, Iterable)} accepts
 * a set of {@link Initializer}s and a root initializer and executes all
 * initializers the root initializer depends on in the order imposed by the
 * depends relation.
 * </p>
 *
 * <p>
 * {@link InitializationService#initialize(Class)} is used to initialize a
 * system. First the {@link CreateInitializersEvent} is raised, which gives all
 * observers the opportunity to add initializers using
 * {@link CreateInitializersEvent#addInitializer(Initializer)} A root
 * initializer depending on all initializers with the provided representing
 * class is created and the initializers are executed.
 * </p>
 *
 *
 * <p>
 * The method {@link InitializationService#createInitializers(Object)} takes an
 * object instance, scans the class of the object for methods annotated with
 * {@link LafInitializer} and creates initializer instances, which will invoke
 * the initializer methods. In addition, if the object implements
 * {@link InitializerProvider} the provided initializers are returned as well.
 * Using this mechanism, when the {@link CreateInitializersEvent} is raised, all
 * singletons are scanned and initializers are created.
 * </p>
 */
@Module
public class InitializationModule {

}
