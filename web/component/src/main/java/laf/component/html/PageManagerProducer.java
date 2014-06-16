package laf.component.html;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@RequestScoped
public class PageManagerProducer {

	private PageManager pageManager;

	public PageManager getPageManager() {
		return pageManager;
	}

	public void setPageManager(PageManager pageManager) {
		this.pageManager = pageManager;
	}

	@Produces
	@RequestScoped
	public PageManager produce() {
		return pageManager;
	}
}
