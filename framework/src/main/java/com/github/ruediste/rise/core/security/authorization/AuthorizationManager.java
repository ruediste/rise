package com.github.ruediste.rise.core.security.authorization;

import com.github.ruediste.salta.jsr330.ImplementedBy;

@ImplementedBy(DefaultAuthorizationManager.class)
public interface AuthorizationManager {

    /**
     * Determine if an {@link AuthorizationRequest} is granted
     */
    boolean isGranted(AuthorizationRequest<?, ?, ?> request);

    /**
     * Check if an {@link AuthorizationRequest} is granted. If the function
     * returns without error, the request was granted.
     * 
     * @throws AuthorizationDeniedException
     *             if the request was not granted
     */
    void checkGranted(AuthorizationRequest<?, ?, ?> request);

}