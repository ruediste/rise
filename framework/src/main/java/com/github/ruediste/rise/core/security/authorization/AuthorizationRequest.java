package com.github.ruediste.rise.core.security.authorization;

import com.github.ruediste.rise.core.security.Environment;
import com.github.ruediste.rise.core.security.Operation;
import com.github.ruediste.rise.core.security.Principal;

/**
 * Request for authorization. Can be granted or denied using the
 * {@link DefaultAuthorizationManager}
 */
public class AuthorizationRequest<TPrincipal extends Principal, TOperation extends Operation, TEnvironment extends Environment> {

    private final TPrincipal principal;
    private final TOperation operation;
    private final TEnvironment environment;

    AuthorizationRequest(TPrincipal principal, TOperation operation,
            TEnvironment environment) {
        super();
        this.principal = principal;
        this.operation = operation;
        this.environment = environment;
    }

    public static <TPrincipal extends Principal, TOperation extends Operation, TEnvironment extends Environment> AuthorizationRequest<TPrincipal, TOperation, TEnvironment> of(
            TPrincipal principal, TOperation operation, TEnvironment environment) {
        return new AuthorizationRequest<>(principal, operation, environment);
    }

    public TPrincipal getSubject() {
        return principal;
    }

    public TOperation getOperation() {
        return operation;
    }

    public TEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public String toString() {
        return "(" + principal + "," + operation + "," + environment + ")";
    }
}
