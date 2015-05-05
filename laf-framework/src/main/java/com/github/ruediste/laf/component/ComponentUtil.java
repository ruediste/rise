package com.github.ruediste.laf.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.rendersnake.Renderable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

@Singleton
public class ComponentUtil {

	@Inject
	HtmlComponentService componentService;

	@Inject
	TemplateUtil templateUtil;

	@Inject
	PageScopeManager pageScopeManager;

	@Inject
	WebRequestInfo webRequestInfo;

	@Inject
	ResourceRenderUtil resourceRenderUtil;

	private ThreadLocal<Component> currentComponent = new ThreadLocal<Component>();

	public long pageId() {
		return pageScopeManager.getId();
	}

	public String getKey(String key) {
		return componentService.calculateKey(getComponent(), key);
	}

	public Component getComponent() {
		Component result = currentComponent.get();
		if (result == null) {
			throw new RuntimeException(
					"Current Component not set. Is CWRenderUtil used outside of the render method of a template?");
		}
		return result;
	}

	public void render(HtmlCanvas html, Component component) throws IOException {
		Component old = currentComponent.get();
		try {
			currentComponent.set(component);
			templateUtil.getTemplate(component).render(component, html);
		} finally {
			currentComponent.set(old);
		}

	}

	public long getComponentNr() {
		return componentService.getComponentNr(getComponent());
	}

	/**
	 * Return the appropriate value for the html element id attribute.
	 */
	public String getComponentId() {
		return "c_" + getComponentNr();
	}

	public long getPageId() {
		return pageScopeManager.getId();
	}

	public String getReloadUrl() {
		return url(webRequestInfo.getReloadPath() + "/" + getPageId());
	}

	public Renderable cssBundle(ResourceOutput css) {
		return resourceRenderUtil.cssBundle(this::url, css);
	}

	public Renderable jsBundle(ResourceOutput js) {
		return resourceRenderUtil.jsBundle(this::url, js);
	}

	public String combineClasses(String... classes) {
		return Arrays.asList(classes).stream()
				.filter(x -> !Strings.isNullOrEmpty(x))
				.map(CharMatcher.WHITESPACE::trimFrom)
				.collect(Collectors.joining(" "));
	}
}
