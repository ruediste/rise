package com.github.ruediste.rise.component.generic;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.google.common.reflect.TypeToken;

@Singleton
public class EditComponents {

    @Inject
    Strategies strategies;

    public class PropertyApi<T> {

        private PropertyInfo property;
        private Optional<Class<? extends Annotation>> qualifier = Optional.empty();

        private PropertyApi(PropertyInfo property) {
            this.property = property;
        }

        public PropertyApi<T> qualifier(Class<? extends Annotation> qualifier) {
            this.qualifier = Optional.of(qualifier);
            return this;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No edit component found for " + property));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            return strategies.getStrategy(EditComponentFactory.class).element(property).cached(property)
                    .get(f -> (Optional) f.getComponent(property.getPropertyType(), Optional.of(property.getName()),
                            Optional.of(property), qualifier));
        }
    }

    public PropertyApi<?> property(PropertyInfo info) {
        return new PropertyApi<>(info);
    }

    public <T, P> PropertyApi<P> property(Class<T> startClass, Function<T, P> propertyAccessor) {
        return new PropertyApi<>(
                PropertyUtil.getPropertyPath(startClass, x -> propertyAccessor.apply(x)).getAccessedProperty());
    }

    @SuppressWarnings("unchecked")
    public <T, P> PropertyApi<P> property(T start, Function<T, P> propertyAccessor) {
        return new PropertyApi<>(PropertyUtil.getPropertyPath(start.getClass(), x -> propertyAccessor.apply((T) x))
                .getAccessedProperty());
    }

    public class InstanceApi<T> {

        private Object start;
        private PropertyPath propertyPath;

        public InstanceApi(Object start, PropertyPath propertyPath) {
            this.start = start;
            this.propertyPath = propertyPath;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(
                    () -> new RuntimeException("No edit component found for " + propertyPath.getAccessedProperty()));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            Optional<EditComponentWrapper<T>> editComponent = (Optional) property(propertyPath.getAccessedProperty())
                    .tryGet();
            editComponent.ifPresent(c -> c.bindValue(() -> (T) propertyPath.evaluate(start)));
            return editComponent;
        }
    }

    /**
     * created edit components will be bound to the accessed property
     */
    @SuppressWarnings("unchecked")
    public <T, P> InstanceApi<P> instance(T start, Function<T, P> propertyAccessor) {
        PropertyPath propertyPath = PropertyUtil.getPropertyPath((Class<T>) start.getClass(),
                (T t) -> propertyAccessor.apply(t));
        return new InstanceApi<P>(start, propertyPath);
    }

    public class TypeApi<T> {

        private TypeToken<T> type;
        private Optional<String> testName = Optional.empty();
        private Optional<Class<? extends Annotation>> qualifier = Optional.empty();

        private TypeApi(TypeToken<T> cls) {
            this.type = cls;
        }

        public EditComponentWrapper<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No edit component found for type " + type));
        }

        public TypeApi<T> testName(String testName) {
            this.testName = Optional.of(testName);
            return this;
        }

        public TypeApi<T> qualifier(Class<? extends Annotation> qualifier) {
            this.qualifier = Optional.of(qualifier);
            return this;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<EditComponentWrapper<T>> tryGet() {
            return (Optional) strategies.getStrategy(EditComponentFactory.class).cached(type)
                    .get(f -> f.getComponent(type, testName, Optional.empty(), qualifier));
        }
    }

    public <T> TypeApi<T> type(Class<T> cls) {
        return new TypeApi<>(TypeToken.of(cls));
    }

    public <T> TypeApi<T> type(TypeToken<T> type) {
        return new TypeApi<>(type);
    }
}
