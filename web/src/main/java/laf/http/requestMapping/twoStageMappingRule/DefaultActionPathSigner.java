package laf.http.requestMapping.twoStageMappingRule;

import laf.actionPath.ActionPath;

public class DefaultActionPathSigner implements ActionPathSigner {

	@Override
	public void sign(ActionPath<String> path) {

	}

	@Override
	public boolean isSignatureValid(ActionPath<String> path) {
		return true;
	}

}
