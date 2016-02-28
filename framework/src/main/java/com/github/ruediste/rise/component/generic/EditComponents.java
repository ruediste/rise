package com.github.ruediste.rise.component.generic;

import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.strategy.Strategies;

@Singleton
public class EditComponents {

    @Inject
    Strategies strategies;

    public class PropertyApi {

        private PropertyInfo property;

        public PropertyApi(PropertyInfo property) {
            this.property = property;
        }

        Optional<Component> tryGet() {
            strategies.getStrategy(EditComponent.class, property.get, null)
        }
    }

    public <T> PropertyApi of(Class<T> startClass,
            Consumer<T> propertyAccessor) {
        return new PropertyApi(
                PropertyUtil.getPropertyPath(startClass, propertyAccessor)
                        .getAccessedProperty());
    }
}
