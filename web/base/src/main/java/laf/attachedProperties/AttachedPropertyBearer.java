package laf.attachedProperties;

/**
 * Interface for classes which can have {@link AttachedProperty
 * AttachedProperties}.
 */
public interface AttachedPropertyBearer {

	/**
	 * Return the property map associated with this property bearer.
	 */
	AttachedPropertyMap getAttachedPropertyMap();
}