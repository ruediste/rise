package com.github.ruediste.rise.component;

import java.util.Collection;

import javax.validation.ConstraintViolation;

public interface ConstraintViolationAware {

    void clearConstraintViolations();

    void setConstraintViolations(Collection<ConstraintViolation<?>> collection);
}
