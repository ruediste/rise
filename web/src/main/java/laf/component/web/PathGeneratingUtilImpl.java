package laf.component.web;

import javax.inject.Inject;

public class PathGeneratingUtilImpl implements PathGeneratingUtil {

	@Inject
	PathGeneratingUtilDependencies pathGeneratingUtilDependencies;

	@Override
	public PathGeneratingUtilDependencies getPathGeneratingUtilDependencies() {
		return pathGeneratingUtilDependencies;
	}

}
