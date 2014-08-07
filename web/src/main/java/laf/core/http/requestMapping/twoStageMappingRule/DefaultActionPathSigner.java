package laf.core.http.requestMapping.twoStageMappingRule;

import laf.core.actionPath.ActionPath;

public class DefaultActionPathSigner implements ActionPathSigner {

	@Override
	public void sign(ActionPath<String> path) {

	}

	@Override
	public boolean isSignatureValid(ActionPath<String> path) {
		return true;
	}

}
