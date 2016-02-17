package com.github.ruediste.rise.core.i18n;

import javax.validation.Payload;

/**
 * Marker interface for messages of validation constraints.
 * 
 * <p>
 * Using this interface is not mandatory, as any labeled payload will be
 * considered, but it's usage is encouraged to facilitate looking up existing
 * messages.
 * 
 * @see RiseValidationMessageInterpolator
 */
public interface ValidationMessage extends Payload {

}
