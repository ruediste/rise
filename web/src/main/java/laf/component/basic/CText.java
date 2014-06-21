package laf.component.basic;

import laf.component.tree.ComponentBase;

public class CText extends ComponentBase<CText> {

	private String text;

	public CText() {
	}

	public CText(String text) {
		this.text = text;

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
