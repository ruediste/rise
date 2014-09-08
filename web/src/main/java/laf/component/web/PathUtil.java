package laf.component.web;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class PathUtil {

	private String reloadPath;

	public void initialize(String reloadPath) {
		this.reloadPath = reloadPath;
	}

	public String getReloadPath() {
		return reloadPath;
	}
}
