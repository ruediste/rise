package com.github.ruediste.rise.core.i18n;

import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.lString.LString;

public class ValidationFailureImpl implements ValidationFailure {

    private final ValidationFailureSeverity severity;
    private final LString message;

    public ValidationFailureImpl(LString message) {
        this(message, ValidationFailureSeverity.ERROR);
    }

    public ValidationFailureImpl(LString message, ValidationFailureSeverity severity) {
        super();
        this.message = message;
        this.severity = severity;
    }

    @Override
    public void render(RiseCanvas<?> html) {
        html.write(message);
    }

    @Override
    public ValidationFailureSeverity getSeverity() {
        return severity;
    }

}
