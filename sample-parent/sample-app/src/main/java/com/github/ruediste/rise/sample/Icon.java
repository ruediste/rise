package com.github.ruediste.rise.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.integration.IconAnnotation;

@IconAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Icon {
    Glyphicon value();
}
