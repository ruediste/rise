package com.github.ruediste.laf.component.web.components;

import java.util.function.Supplier;

import com.github.ruediste.laf.component.web.CWViewUtil;
import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.base.InstanceFactory;

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
		add(new CRender(html -> html.write(text)));
	}

	public CLink(String text, Supplier<String> destinationUrlSupplier) {
		this();
		this.destinationUrlSupplier = destinationUrlSupplier;
		add(new CRender(html -> html.write(text)));
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
