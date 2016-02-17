package com.github.ruediste.rise.core.i18n;

import com.github.ruediste1.i18n.lString.LString;

public interface ValidationFailure {

    LString getMessage();

    ValidationFailureSeverity getSeverity();
}
