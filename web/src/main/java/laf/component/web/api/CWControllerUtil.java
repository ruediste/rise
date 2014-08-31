package laf.component.web.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import laf.component.core.ControllerUtilBase;
import laf.component.web.PathGeneratingUtil;
import laf.component.web.PathGeneratingUtilDependencies;

@RequestScoped
public class CWControllerUtil extends ControllerUtilBase implements
PathGeneratingUtil {

	@Inject
	PathGeneratingUtilDependencies pathGeneratingUtilDependencies;

	@Override
	public PathGeneratingUtilDependencies getPathGeneratingUtilDependencies() {
		return pathGeneratingUtilDependencies;
	}
}
