package com.github.ruediste.rise.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;

@IconAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GlyphiconIcon {
    Glyphicon value();
}
