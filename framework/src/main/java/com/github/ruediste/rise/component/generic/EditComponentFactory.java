package com.github.ruediste.rise.component.generic;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public interface EditComponentFactory {
    Optional<EditComponent> getComponent(AnnotatedElement e);
}
