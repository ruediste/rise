package laf.controllerInfo;

import java.lang.reflect.Method;
import java.util.Collection;

import laf.attachedProperties.AttachedPropertyBearer;

/**
 * Contains information about an action method. An action method is a method of
 * a controller, which can be invoked via HTTP to generate a response.
 *
 * Implementations of this interface may not be modified after initialization.
 */
public interface ActionMethodInfo extends AttachedPropertyBearer {

	/**
	 * Get the info of the controller defining this method
	 */
	ControllerInfo getControllerInfo();

	/**
	 * Get the parameters of this method
	 */
	Collection<ParameterInfo> getParameters();

	/**
	 * Return the underlying method.
	 */
	Method getMethod();

	/**
	 * Return the name of this action method. Must be unique within the
	 * Controller.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Return true if this method returns an embedded controller.
	 *
	 * @return
	 */
	boolean returnsEmbeddedController();

	/**
	 * Return a signature including method name, argument types and return type
	 * 
	 * @return
	 */
	String getSignature();
}
