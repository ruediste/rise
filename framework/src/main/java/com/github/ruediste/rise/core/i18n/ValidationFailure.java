package com.github.ruediste.rise.core.i18n;

import java.util.function.Consumer;

import com.github.ruediste.rise.integration.RiseCanvas;

public interface ValidationFailure {

    void render(RiseCanvas<?> html);

    ValidationFailureSeverity getSeverity();

    public static ValidationFailure error(Runnable renderer) {
        return create(ValidationFailureSeverity.ERROR, renderer);
    }

    public static ValidationFailure error(Consumer<RiseCanvas<?>> renderer) {
        return create(ValidationFailureSeverity.ERROR, renderer);
    }

    public static ValidationFailure warning(Runnable renderer) {
        return create(ValidationFailureSeverity.WARNING, renderer);
    }

    public static ValidationFailure warning(Consumer<RiseCanvas<?>> renderer) {
        return create(ValidationFailureSeverity.WARNING, renderer);
    }

    public static ValidationFailure info(Runnable renderer) {
        return create(ValidationFailureSeverity.INFO, renderer);
    }

    public static ValidationFailure info(Consumer<RiseCanvas<?>> renderer) {
        return create(ValidationFailureSeverity.INFO, renderer);
    }

    public static ValidationFailure create(ValidationFailureSeverity severity, Runnable renderer) {
        return create(severity, html -> renderer.run());

    }

    public static ValidationFailure create(ValidationFailureSeverity severity, Consumer<RiseCanvas<?>> renderer) {
        return new ValidationFailure() {

            @Override
            public void render(RiseCanvas<?> html) {
                renderer.accept(html);
            }

            @Override
            public ValidationFailureSeverity getSeverity() {
                return severity;
            }
        };
    }
}
