package com.github.ruediste.rise.component.components;

/**
 * Show a check box
 */
@DefaultTemplate(CCheckBoxTemplate.class)
public class CCheckBox extends CInputBase<CCheckBox> {

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
