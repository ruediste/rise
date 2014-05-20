package laf.requestProcessing;

/**
 * Interface which can be injected in order to obtain the current controller.
 */
public interface CurrentController {

	/**
	 * Returns the current controller
	 */
	Object get();
}
