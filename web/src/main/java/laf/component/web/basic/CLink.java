package laf.component.web.basic;

import java.util.function.Supplier;

import laf.component.core.basic.CText;
import laf.component.core.basic.MultiChildrenComponent;
import laf.component.web.CWViewUtil;
import laf.core.base.ActionResult;
import laf.core.base.InstanceFactory;

/**
 * A link to another page.
 */
public class CLink extends MultiChildrenComponent<CLink> {

	private Supplier<String> destinationUrlSupplier;
	private CWViewUtil viewUtil;

	public CLink() {
		viewUtil = InstanceFactory.getInstance(CWViewUtil.class);
	}

	public CLink(ActionResult destination) {
		this();
		destinationUrlSupplier = () -> viewUtil.url(destination);
	}

	public CLink(String text, ActionResult destination) {
		this(destination);
		add(new CText(text));
	}

	public CLink(String text, Supplier<String> destinationUrlSupplier) {
		this();
		this.destinationUrlSupplier = destinationUrlSupplier;
		add(new CText(text));
	}

	public CLink withDestination(ActionResult destination) {
		destinationUrlSupplier = () -> viewUtil.url(destination);
		return self();
	}

	public String getDestinationUrl() {
		return destinationUrlSupplier.get();
	}

	public CLink withDestinationUrlSupplier(
			Supplier<String> destinationUrlSupplier) {
		this.destinationUrlSupplier = destinationUrlSupplier;
		return this;
	}
}
