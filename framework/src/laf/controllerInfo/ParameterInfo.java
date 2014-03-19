package laf.controllerInfo;

import java.lang.reflect.Type;

import laf.attachedProperties.AttachedPropertyBearer;

/**
 * Information of a parameter to an action method. Implementations of this
 * interface may not be modified after initialization.
 * 
 * @see ActionMethodInfo
 */
public interface ParameterInfo extends AttachedPropertyBearer {

	/**
	 * The type of this parameter
	 */
	Type getType();

	/**
	 * Return the method declaring this parameter.
	 */
	ActionMethodInfo getMethod();
}
