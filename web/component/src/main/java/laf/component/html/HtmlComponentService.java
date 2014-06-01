package laf.component.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.attachedProperties.AttachedProperty;
import laf.component.core.Component;
import laf.component.core.ComponentTreeUtil;
import laf.component.core.ComponentView;
import laf.configuration.ConfigurationValue;
import laf.http.ContentType;

import org.rendersnake.HtmlCanvas;

@ApplicationScoped
public class HtmlComponentService {

	private static Charset UTF8 = Charset.forName("UTF-8");

	private final AttachedProperty<Component, Long> componentId = new AttachedProperty<>();
	private final AttachedProperty<ComponentView<?>, Map<Long, Component>> componentIdMap = new AttachedProperty<>();
	private final AttachedProperty<ComponentView<?>, Long> maxComponentId = new AttachedProperty<>();

	@Inject
	Instance<RenderUtilImpl> renderUtilInstance;

	@Inject
	ConfigurationValue<ContentType> contentType;

	public String calculateKey(Component component, String key) {
		return "c_" + componentId.get(component) + "_" + key;
	}

	public void renderPage(ComponentView<?> view, Component rootComponent,
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
				HtmlComponentService.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			RenderUtilImpl renderUtil = renderUtilInstance.get();
			renderUtil.setComponent(rootComponent);
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

	public long getComponentId(Component component) {
		return componentId.get(component);
	}

	public Component getComponent(ComponentView<?> view, long componentId) {
		return componentIdMap.get(view).get(componentId);
	}
}
