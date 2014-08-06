package laf.controllerInfo;

import java.lang.reflect.Method;
import java.util.Collection;

import laf.base.attachedProperties.AttachedPropertyBearer;

/**
 * Provides information about a controller. A controller contains action
 * methods, which can be called via requests.
 * 
 * Implementations of this interface may not be modified after initialization.
 */
public interface ControllerInfo extends AttachedPropertyBearer {

	/**
	 * Return the name of this controller, without package
	 */
	String getName();

	/**
	 * Return the package of this controller. If the controller is in the
	 * default package, the empty string is returned.
	 */
	String getPackage();

	/**
	 * Return the package and the name of this controller.
	 */
	String getQualifiedName();

	/**
	 * Return the underlying controller class.
	 */
	Class<?> getControllerClass();

	/**
	 * Return the action method info of the given name, or null if none is
	 * found.
	 */
	ActionMethodInfo getActionMethodInfo(String name);

	/**
	 * Return all action methods for this controller
	 */
	Collection<ActionMethodInfo> getActionMethodInfos();

	/**
	 * Return the action method info of the given method, or null if none is
	 * found.
	 */
	ActionMethodInfo getActionMethodInfo(Method method);

	/**
	 * Return the type of this controller
	 */
	Class<? extends ControllerType> getType();

	/**
	 * Return true if this controller is an embedded controller
	 * 
	 * @return
	 */
	boolean isEmbeddedController();
}
