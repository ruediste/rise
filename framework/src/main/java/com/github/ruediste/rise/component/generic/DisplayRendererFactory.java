package com.github.ruediste.rise.component.generic;

import java.util.Optional;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.google.common.reflect.TypeToken;

public interface DisplayRendererFactory extends Strategy {

    /**
     * @param info
     *            property info to create the component for. Can be null if only
     *            the class is known
     */
    <T> Optional<DisplayRenderer<T>> getRenderer(TypeToken<T> type, Optional<PropertyInfo> info);

}
