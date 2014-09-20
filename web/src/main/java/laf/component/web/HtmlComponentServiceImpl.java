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
import laf.core.base.attachedProperties.AttachedProperty;

import org.rendersnake.HtmlCanvas;

@ApplicationScoped
public class HtmlComponentServiceImpl implements HtmlComponentService {

	private static Charset UTF8 = Charset.forName("UTF-8");

	private final AttachedProperty<Component, Long> componentNr = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Map<Long, Component>> componentIdMap = new AttachedProperty<>();
	private final AttachedProperty<CView<?>, Long> maxComponentNr = new AttachedProperty<>();

	@Inject
	CWRenderUtil cwRenderUtil;

	@Inject
	ComponentWebRequestInfo requestInfo;

	@Override
	public String calculateKey(Component component, String key) {
		return "c_" + componentNr.get(component) + "_" + key;
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
				HtmlComponentServiceImpl.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			cwRenderUtil.render(canvas, rootComponent);
			writer.close();
			byte[] byteArray = stream.toByteArray();

			// send answer
			if (requestInfo.getContentType() == null) {
				response.setContentType("text/html; charset=UTF-8");
			} else {
				response.setContentType(requestInfo.getContentType());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(byteArray);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
	}

	@Override
	public long getComponentNr(Component component) {
		return componentNr.get(component);
	}

	@Override
	public Component getComponent(CView<?> view, long componentId) {
		return componentIdMap.get(view).get(componentId);
	}
}
