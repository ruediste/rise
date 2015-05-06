package com.github.ruediste.laf.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.tree.ComponentTreeUtil;
import com.github.ruediste.laf.component.web.components.template.CWTemplate;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.actionInvocation.InvocationActionResult;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.core.web.assetPipeline.AssetRenderUtil;
import com.github.ruediste.laf.mvc.web.CoreUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

@Singleton
public class ComponentUtil {

	private final AttachedProperty<Component, Long> componentNr = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Map<Long, Component>> componentIdMap = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Long> maxComponentNr = new AttachedProperty<>();

	@Inject
	PageInfo pageInfo;

	@Inject
	TemplateIndex templateIndex;

	@Inject
	AssetRenderUtil assetRenderUtil;

	@Inject
	ComponentConfiguration componentConfiguration;

	@Inject
	CoreUtil coreUtil;

	public PathInfo toPathInfo(ActionInvocation<Object> invocation) {
		return componentConfiguration.mapper().generate(
				coreUtil.toStringInvocation(invocation));
	}

	public HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
		return new HttpRequestImpl(toPathInfo(invocation));
	}

	public long pageId() {
		return pageInfo.getPageId();
	}

	public String getKey(Component component, String key) {
		return "c_" + componentNr.get(component) + "_" + key;
	}

	public long getComponentNr(Component component) {
		return componentNr.get(component);
	}

	public Component getComponent(CView<?> view, long componentId) {
		return componentIdMap.get(view).get(componentId);
	}

	/**
	 * Set the component number of all children of the root component which do
	 * not have a number yet
	 */
	public byte[] renderComponents(CView<?> view, Component rootComponent) {
		{
			// set the component IDs
			Map<Long, Component> map;
			if (componentIdMap.isSet(view)) {
				map = componentIdMap.get(view);
			} else {
				map = new HashMap<>();
				componentIdMap.set(view, map);
			}
			long nr;
			{
				Long tmp = maxComponentNr.get(view);
				if (tmp == null) {
					tmp = 0L;
				}
				nr = tmp;
			}
			for (Component c : ComponentTreeUtil.subTree(rootComponent)) {
				if (!componentNr.isSet(c)) {
					map.put(nr, c);
					componentNr.set(c, nr++);
				}
			}
			maxComponentNr.set(view, nr);
		}
		// render the view first, to detect possible errors
		// before rendering the result
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				Charsets.UTF_8);
		try {
			HtmlCanvas canvas = new HtmlCanvas(writer);
			render(rootComponent, canvas);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while components view", e);
		}
		return stream.toByteArray();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void render(Component component, HtmlCanvas html) {
		try {
			((CWTemplate) templateIndex.getTemplate(component.getClass()))
					.render(component, html);
		} catch (IOException e) {
			throw new RuntimeException("Error while components view", e);
		}
	}

	/**
	 * Return the appropriate value for the html element id attribute.
	 */
	public String getComponentId(Component component) {
		return "c_" + getComponentNr(component);
	}

	public String getReloadUrl() {
		return coreUtil.url(componentConfiguration.getReloadPath() + "/"
				+ pageId());
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return assetRenderUtil.renderJs(coreUtil::url, output);
	}

	public Renderable cssLinks(AssetBundleOutput output) {
		return assetRenderUtil.renderCss(coreUtil::url, output);
	}

	public String combineCssClasses(String... classes) {
		return Arrays.asList(classes).stream()
				.filter(x -> !Strings.isNullOrEmpty(x))
				.map(CharMatcher.WHITESPACE::trimFrom)
				.collect(Collectors.joining(" "));
	}

	public String url(ActionResult path) {
		return coreUtil.url(toPathInfo((InvocationActionResult) path));
	}

}
