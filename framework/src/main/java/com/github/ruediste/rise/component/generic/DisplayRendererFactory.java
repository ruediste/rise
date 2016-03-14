package com.github.ruediste.rise.component.generic;

import java.util.Optional;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.salta.jsr330.ImplementedBy;

@ImplementedBy(DefaultDisplayRendererFactory.class)
public interface DisplayRendererFactory extends Strategy {

    /**
     * @param info
     *            property info to create the component for. Can be null if only
     *            the class is known
     */
    Optional<DisplayRenderer<?>> getRenderer(Class<?> cls, Optional<PropertyInfo> info);

}
