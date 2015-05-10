package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.CButtonHtmlTemplate;

@DefaultTemplate(CButtonHtmlTemplate.class)
public class CButton extends MultiChildrenComponent<CButton> {
	private Runnable handler;

	public CButton() {
	}

	public CButton(String text) {
		add(new CRender(html -> html.write(text)));
	}

	public CButton handler(Runnable handler) {
		this.handler = handler;
		return this;
	}

	public Runnable getHandler() {
		return handler;
	}

}
