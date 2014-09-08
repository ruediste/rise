package laf.component.web;

import javax.inject.Inject;

public class PathUtilInitializer implements Runnable {

	@Inject
	PathUtil pathUtil;
	private String reloadPath;

	public void initialize(String reloadPath) {
		this.reloadPath = reloadPath;
	}

	@Override
	public void run() {
		pathUtil.initialize(reloadPath);

	}

}
