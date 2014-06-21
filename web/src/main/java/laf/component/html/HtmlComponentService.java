package laf.component.html;

import javax.servlet.http.HttpServletResponse;

import laf.component.core.ComponentView;
import laf.component.tree.Component;

public interface HtmlComponentService {

	public abstract String calculateKey(Component component, String key);

	public abstract void renderPage(ComponentView<?> view,
			Component rootComponent, HttpServletResponse response);

	public abstract long getComponentId(Component component);

	public abstract Component getComponent(ComponentView<?> view,
			long componentId);

}