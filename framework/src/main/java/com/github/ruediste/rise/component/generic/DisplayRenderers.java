package com.github.ruediste.rise.component.generic;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.component.binding.transformers.Transformers;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.google.common.reflect.TypeToken;

@Singleton
public class DisplayRenderers {

    @Inject
    Strategies strategies;

    @Inject
    Transformers transformers;

    public class PropertyApi<T> {

        private PropertyInfo property;

        private PropertyApi(PropertyInfo property) {
            this.property = property;
        }

        public DisplayRenderer<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No display renderer found for " + property));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<DisplayRenderer<T>> tryGet() {
            return strategies.getStrategy(DisplayRendererFactory.class).element(property).cached(property)
                    .get(f -> (Optional) f.getRenderer(property.getPropertyType(), Optional.of(property)));
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
        PropertyPath propertyPath = PropertyUtil.getPropertyPath((Class<T>) start.getClass(),
                (T t) -> propertyAccessor.apply(t));
        return (PropertyApi<P>) property(propertyPath.getAccessedProperty());
    }

    public class TypeApi<T> {

        private TypeToken<T> type;

        private TypeApi(TypeToken<T> type) {
            this.type = type;
        }

        public DisplayRenderer<T> get() {
            return tryGet().orElseThrow(() -> new RuntimeException("No display renderer found for type " + type));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Optional<DisplayRenderer<T>> tryGet() {
            return strategies.getStrategy(DisplayRendererFactory.class).cached(type)
                    .get(f -> f.getRenderer(type, Optional.empty()));
        }
    }

    public <T> TypeApi<T> type(Class<T> cls) {
        return new TypeApi<>(TypeToken.of(cls));
    }

    public <T> TypeApi<T> type(TypeToken<T> type) {
        return new TypeApi<>(type);
    }

    @PostConstruct
    void postConstruct() {
        strategies.putStrategy(new DisplayRendererFactory() {

            @Override
            public <T> Optional<DisplayRenderer<T>> getRenderer(TypeToken<T> type, Optional<PropertyInfo> info) {

                if (byte[].class.equals(type.getType())) {
                    return Optional.of((html, value) -> html
                            .write(transformers.byteArrayToHexStringTransformer.transform((byte[]) value)));
                }

                return Optional.of((html, value) -> html.write(Objects.toString(value)));
            }
        });
    }

}
