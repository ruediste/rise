package com.github.ruediste.rise.component.generic;

import com.github.ruediste.rise.component.tree.Component;

public interface EditComponent extends Component {
    Object getValue();

    EditComponent setValue(Object value);

}
