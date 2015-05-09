package com.github.ruediste.rise.component.web.components;

import com.github.ruediste.rise.component.web.components.template.CTextFieldFormGroupHtmlTemplate;

@DefaultTemplate(CTextFieldFormGroupHtmlTemplate.class)
public class CTextFieldFormGroup extends CFormGroup<CTextFieldFormGroup> {

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
