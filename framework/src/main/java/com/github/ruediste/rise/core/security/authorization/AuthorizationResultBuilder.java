package com.github.ruediste.rise.core.security.authorization;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationResultBuilder {

    private List<AuthorizationFailure> failures = new ArrayList<AuthorizationFailure>();

    public AuthorizationResultBuilder add(AuthorizationFailure failure) {
        failures.add(failure);
        return this;
    }

    public AuthorizationResult build() {
        if (failures.isEmpty())
            return AuthorizationResult.authorized();
        else {
            AuthorizationResult result = AuthorizationResult.failure(failures);
            failures = null;
            return result;
        }
    }
}
