package com.github.ruediste.rise.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonStyle;

/**
 * Set the button style to use when rendering a action method or action
 * invocation as button.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ButtonStyle {
    B_ButtonStyle value();
}
