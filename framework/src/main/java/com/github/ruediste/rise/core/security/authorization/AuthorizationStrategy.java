package com.github.ruediste.rise.core.security.authorization;

import java.util.Optional;

import com.github.ruediste.rise.core.security.Environment;
import com.github.ruediste.rise.core.security.Operation;
import com.github.ruediste.rise.core.security.Principal;

/**
 * Strategy to authorize {@link AuthorizationRequest}s, registered with the
 * {@link DefaultAuthorizationManager}.
 */
public interface AuthorizationStrategy<TPrincipal extends Principal, TOperation extends Operation, TEnvironment extends Environment> {

    /**
     * Determine if this strategy grants or denies the
     * {@link AuthorizationRequest}. If {@link Optional#empty()} is returned,
     * the next registered strategy is used. If no strategy matches, the request
     * is denied.
     */
    Optional<Boolean> isGranted(
            AuthorizationRequest<TPrincipal, TOperation, TEnvironment> request);
}
