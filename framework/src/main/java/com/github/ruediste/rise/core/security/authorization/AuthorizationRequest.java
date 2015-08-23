package com.github.ruediste.rise.core.security.authorization;

import com.github.ruediste.rise.core.security.Environment;
import com.github.ruediste.rise.core.security.Operation;
import com.github.ruediste.rise.core.security.Subject;

/**
 * Request for authorization. Can be granted or denied using the
 * {@link DefaultAuthorizationManager}
 */
public class AuthorizationRequest<TSubject extends Subject, TOperation extends Operation, TEnvironment extends Environment> {

    private final TSubject subject;
    private final TOperation operation;
    private final TEnvironment environment;

    AuthorizationRequest(TSubject subject, TOperation operation,
            TEnvironment environment) {
        super();
        this.subject = subject;
        this.operation = operation;
        this.environment = environment;
    }

    public static <TSubject extends Subject, TOperation extends Operation, TEnvironment extends Environment> AuthorizationRequest<TSubject, TOperation, TEnvironment> of(
            TSubject subject, TOperation operation, TEnvironment environment) {
        return new AuthorizationRequest<>(subject, operation, environment);
    }

    public TSubject getSubject() {
        return subject;
    }

    public TOperation getOperation() {
        return operation;
    }

    public TEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public String toString() {
        return "(" + subject + "," + operation + "," + environment + ")";
    }
}
