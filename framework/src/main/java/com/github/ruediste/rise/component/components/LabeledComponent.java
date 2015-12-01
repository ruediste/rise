package com.github.ruediste.rise.component.components;

import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public interface LabeledComponent<TSelf extends LabeledComponent<TSelf>> {

    LString getLabel(LabelUtil labelUtil);
}
