package laf.httpRequestMapping.defaultRule;

import laf.controllerInfo.ControllerInfo;

/**
 * Strategy used to create an unique identifer String from a
 * {@link ControllerInfo}.
 */
public interface ControllerIdentifierStrategy {

	public String generateIdentifier(ControllerInfo info);
}
