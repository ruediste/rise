package laf.initialization;

import java.util.Collection;
import java.util.Set;

/**
 * Represents an initializer which is {@link #run()} during initialization
 *
 * @see InitializationModule
 */
public interface Initializer {

	/**
	 * Return all dependency relation this initializer declares between this and
	 * the other initializer. There might be additional relations declared by
	 * the other initializer.
	 */
	Collection<InitializerDependsRelation> getDeclaredRelations(
			Initializer other);

	/**
	 * Return all the representing classes of initializers this initializer
	 * declares a depends relation to. This information is used to reduce the
	 * time complexity of determining the depends relation. Return null if the
	 * set of classes can not be determined.
	 */
	Set<Class<?>> getRelatedRepresentingClasses();

	/**
	 * Return the class of the component which defined this initializer.
	 */
	Class<?> getRepresentingClass();

	/**
	 * Runs this initializer
	 */
	void run();
}
