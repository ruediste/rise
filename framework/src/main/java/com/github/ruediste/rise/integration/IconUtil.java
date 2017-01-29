package com.github.ruediste.rise.integration;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.c3java.method.MethodUtil;
import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;

/**
 * Utility class to retrieve icons specified via an {@link IconAnnotation}.
 */
public class IconUtil {

    public interface IconResolver {
        Renderable<Html5Canvas<?>> resolve(Method initiatingMethod, Method annotatedMethod, Annotation annotation,
                Renderable<Html5Canvas<?>> value);
    }

    public IconUtil() {
    }

    public <T> Renderable<Html5Canvas<?>> getIcon(Class<T> cls, Consumer<T> accessor) {
        Method method = MethodInvocationRecorder.getLastInvocation(cls, accessor).getMethod();
        return getIcon(method);
    }

    public Renderable<Html5Canvas<?>> getIcon(Method method) {
        return tryGetIcon(method).orElseThrow(() -> new RuntimeException("No icon annotation found for " + method));
    }

    public <T> Optional<Renderable<Html5Canvas<?>>> tryGetIcon(Class<T> cls, Consumer<T> accessor) {
        return tryGetIcon(MethodInvocationRecorder.getLastInvocation(cls, accessor).getMethod());
    }

    public Optional<Renderable<Html5Canvas<?>>> tryGetIcon(Method method) {
        for (Method m : MethodUtil.getDeclarations(method)) {
            Optional<Renderable<Html5Canvas<?>>> value = tryGetIcon((AnnotatedElement) m);
            if (value.isPresent())
                return value;
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<Renderable<Html5Canvas<?>>> tryGetIcon(AnnotatedElement element) {

        for (Annotation a : StereotypeHelper.getAllAnnotations(element)) {
            if (!a.annotationType().isAnnotationPresent(IconAnnotation.class))
                continue;
            for (Method attribute : a.annotationType().getDeclaredMethods()) {
                if (!Renderable.class.isAssignableFrom(attribute.getReturnType()))
                    continue;
                Renderable<Html5Canvas<?>> value;
                try {
                    value = (Renderable<Html5Canvas<?>>) attribute.invoke(a);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException("Error while reading icon annotation value", e);
                }
                if (value != null)
                    return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}
