package laf.controllerInfo;

import java.lang.reflect.Type;

import laf.attachedProperties.AttachedPropertyBearer;

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
	 * Return the {@link Type} of the underlying controller class.
	 */
	Type getType();
}
