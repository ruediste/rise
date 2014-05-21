package laf.httpRequestParsing.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathParameter;

/**
 * Strategy to sign and verify the signature of string {@link ActionPath}s. The
 * signature is represented as {@link ActionPathParameter}.
 */
public interface ActionPathSigner {

	/**
	 * Parameter to represent the hash of an action path signature
	 */
	static final ActionPathParameter hash = new ActionPathParameter("hash");

	/**
	 * Parameter to represent the salt of an action path signature
	 */
	static final ActionPathParameter salt = new ActionPathParameter("salt");

	/**
	 * Set or overwrite the signature of the action path
	 */
	void sign(ActionPath<String> path);

	/**
	 * Verify the signature of the action path
	 */
	boolean isSignatureValid(ActionPath<String> path);
}
