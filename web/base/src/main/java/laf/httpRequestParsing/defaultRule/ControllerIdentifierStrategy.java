package laf.httpRequestParsing.defaultRule;

import laf.controllerInfo.ControllerInfo;

import com.google.common.base.Function;

/**
 * Strategy used to create an unique identifer String from a
 * {@link ControllerInfo}.
 */
public interface ControllerIdentifierStrategy extends
Function<ControllerInfo, String> {

	/**
	 * Return an unique identifier for the supplied {@link ControllerInfo}
	 */
	@Override
	public String apply(ControllerInfo info);
}
