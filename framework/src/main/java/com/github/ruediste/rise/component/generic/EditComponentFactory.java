package com.github.ruediste.rise.component.generic;

import java.util.Optional;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.salta.jsr330.ImplementedBy;

@ImplementedBy(DefaultEditComponentFactory.class)
public interface EditComponentFactory extends Strategy {

    /**
     * @param testName
     *            test name for the component. The name of the property if a
     *            property info is present
     * @param info
     *            property info to create the component for. Can be null if only
     *            the class is known
     */
    Optional<EditComponentWrapper<?>> getComponent(Class<?> cls, Optional<String> testName,
            Optional<PropertyInfo> info);

}
