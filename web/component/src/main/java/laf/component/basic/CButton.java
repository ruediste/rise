package laf.component.basic;

import laf.component.core.ComponentBase;

public class CButton extends ComponentBase<CButton> {
	private String text;

	public CButton(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
