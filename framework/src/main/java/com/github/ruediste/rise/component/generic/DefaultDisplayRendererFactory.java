package com.github.ruediste.rise.component.generic;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.transformers.Transformers;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.google.common.reflect.TypeToken;

@Singleton
public class DefaultDisplayRendererFactory implements DisplayRendererFactory {

    @Inject
    Strategies strategies;

    @Inject
    Transformers transformers;

    @Override
    public <T> Optional<DisplayRenderer<T>> getRenderer(TypeToken<T> type, Optional<PropertyInfo> info) {

        if (byte[].class.equals(type)) {
            return Optional.of((html, value) -> html
                    .write(transformers.byteArrayToHexStringTransformer.transform((byte[]) value)));
        }

        return Optional.of((html, value) -> html.write(Objects.toString(value)));
    }

}
