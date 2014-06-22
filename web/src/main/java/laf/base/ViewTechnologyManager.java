package laf.base;

import javax.enterprise.context.RequestScoped;

/**
 * References the {@link ViewTechnology} used for a request
 */
@RequestScoped
public class ViewTechnologyManager {

	private Class<? extends ViewTechnology> viewTechnology;

	public Class<? extends ViewTechnology> getViewTechnology() {
		return viewTechnology;
	}

	public void setViewTechnology(Class<? extends ViewTechnology> viewTechnology) {
		this.viewTechnology = viewTechnology;
	}

}
