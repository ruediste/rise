package com.github.ruediste.rise.component.generic;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.transformers.Transformers;

@Singleton
public class DefaultDisplayRendererFactory implements DisplayRendererFactory {

    @Inject
    Transformers transformers;

    @Override
    public Optional<DisplayRenderer<?>> getRenderer(Class<?> cls, Optional<PropertyInfo> info) {

        if (byte[].class.equals(cls)) {
            return Optional.of((html, value) -> html
                    .write(transformers.byteArrayToHexStringTransformer.transform((byte[]) value)));

        }
        return Optional.of((html, value) -> html.write(Objects.toString(value)));
    }

}
