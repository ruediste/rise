package com.github.ruediste.rise.component.web.components;

import com.github.ruediste.rise.component.web.components.template.CLinkHtmlTemplate;
import com.github.ruediste.rise.core.ActionResult;

/**
 * A link to another page.
 */
@DefaultTemplate(CLinkHtmlTemplate.class)
public class CLink extends MultiChildrenComponent<CLink> {

	private ActionResult destination;

	public CLink() {
	}

	public CLink(ActionResult destination) {
		this();
		this.destination = destination;
	}

	public CLink(String text, ActionResult destination) {
		this(destination);
		add(new CRender(html -> html.write(text)));
	}

	public CLink withDestination(ActionResult destination) {
		this.destination = destination;
		return self();
	}

	public ActionResult getDestination() {
		return destination;
	}
}
