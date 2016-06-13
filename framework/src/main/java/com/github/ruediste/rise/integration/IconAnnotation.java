package com.github.ruediste.rise.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;

/**
 * Meta annotation for annotations declaring Icons for methods. Each annotation
 * member needs to be an enum extending {@link Renderable}. Using
 * {@link IconUtil}, the single (first) non-null value can be retrieved. Sample
 * Annotation:
 * 
 * <pre>
 * &#64;IconAnnotation
 * &#064;Retention(RetentionPolicy.RUNTIME)
 * &#064;Target(ElementType.METHOD)
 * public @interface Icon {
 *     Glyphicon value();
 * }
 * </pre>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface IconAnnotation {
}
