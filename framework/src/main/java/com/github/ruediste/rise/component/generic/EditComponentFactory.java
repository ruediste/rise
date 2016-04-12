package com.github.ruediste.rise.component.generic;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.google.common.reflect.TypeToken;

public interface EditComponentFactory extends Strategy {

    class EditComponentSpecification {
        public TypeToken<?> type;
        public boolean nullable;
        /**
         * test name for the component. The name of the property if a property
         * info is present
         */
        public Optional<String> testName;
        /**
         * property info to create the component for. Can be null if only the
         * class is known
         */
        public Optional<PropertyInfo> info;
        /**
         * qualifier for the persistence unit to be used (if applicable)
         */
        public Optional<Class<? extends Annotation>> qualifier;

        public EditComponentSpecification(TypeToken<?> type, boolean nullable, Optional<String> testName,
                Optional<PropertyInfo> info, Optional<Class<? extends Annotation>> qualifier) {
            super();
            this.type = type;
            this.nullable = nullable;
            this.testName = testName;
            this.info = info;
            this.qualifier = qualifier;
        }

    }

    Optional<EditComponentWrapper<?>> getComponent(EditComponentSpecification spec);

}
