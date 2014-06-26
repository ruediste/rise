package laf.component.basic;

import laf.base.ActionResult;

/**
 * A link to another page.
 */
public class CLink extends MultiChildrenComponent<CLink> {

	private ActionResult destination;

	public CLink() {
	}

	public CLink(ActionResult destination) {
		this.destination = destination;
	}

	public CLink(String text, ActionResult destination) {
		this.destination = destination;
		add(new CText(text));
	}

	public ActionResult getDestination() {
		return destination;
	}

	public CLink withDestination(ActionResult destination) {
		this.destination = destination;
		return self();
	}
}
