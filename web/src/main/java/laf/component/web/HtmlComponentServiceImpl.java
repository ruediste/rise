package laf.component.web;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.component.core.api.CView;
import laf.component.core.tree.Component;
import laf.component.core.tree.ComponentTreeUtil;
import laf.component.web.api.CWRenderUtil;
import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.configuration.ConfigurationValue;
import laf.core.defaultConfiguration.ContentTypeCP;

import org.rendersnake.HtmlCanvas;

@ApplicationScoped
public class HtmlComponentServiceImpl implements HtmlComponentService {

	private static Charset UTF8 = Charset.forName("UTF-8");

	private final AttachedProperty<Component, Long> componentId = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Map<Long, Component>> componentIdMap = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Long> maxComponentId = new AttachedProperty<>();

	@Inject
	ConfigurationValue<ContentTypeCP> contentType;

	@Inject
	CWRenderUtil renderUtil;

	@Override
	public String calculateKey(Component component, String key) {
		return "c_" + componentId.get(component) + "_" + key;
	}

	@Override
	public void renderPage(CView<?> view, Component rootComponent,
			HttpServletResponse response) {

		// set the component IDs
		{
			Map<Long, Component> map;
			if (componentIdMap.isSet(view)) {
				map = componentIdMap.get(view);
			} else {
				map = new HashMap<>();
				componentIdMap.set(view, map);
			}
			long id;
			{
				Long tmp = maxComponentId.get(view);
				if (tmp == null) {
					tmp = 0L;
				}
				id = tmp;
			}
			for (Component c : ComponentTreeUtil.subTree(rootComponent)) {
				if (!componentId.isSet(c)) {
					map.put(id, c);
					componentId.set(c, id++);
				}
			}
			maxComponentId.set(view, id);
		}

		// render the view first, to detect possible errors
		// before rendering the result
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				HtmlComponentServiceImpl.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			renderUtil.render(canvas, rootComponent);
			writer.close();
			byte[] byteArray = stream.toByteArray();

			// send answer
			response.setContentType(contentType.value().get()
					+ "; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(byteArray);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
	}

	@Override
	public long getComponentId(Component component) {
		return componentId.get(component);
	}

	@Override
	public Component getComponent(CView<?> view, long componentId) {
		return componentIdMap.get(view).get(componentId);
	}
}
