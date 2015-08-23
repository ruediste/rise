package com.github.ruediste.rise.core.security;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.core.security.authorization.AuthorizationRequest;

/**
 * Marker interface for environments.
 * 
 * <p>
 * Environments contain additional information for {@link AuthorizationRequest}s
 * which is not part of the {@link Subject} or the {@link Operation}
 * 
 */
public class Environment extends AttachedPropertyBearerBase {

}
