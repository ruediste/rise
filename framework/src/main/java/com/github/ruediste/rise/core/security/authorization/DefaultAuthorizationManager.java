package com.github.ruediste.rise.core.security.authorization;

import java.util.Optional;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.security.Environment;
import com.github.ruediste.rise.core.security.Operation;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.util.Tripple;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.reflect.TypeToken;

/**
 * Determines if {@link AuthorizationRequest}s are granted based on registered
 * {@link AuthorizationStrategy}s.
 * 
 * <p>
 * Strategies are registered for a combination of subject, operation and
 * environment class. If multiple strategies are registered, they are tried in
 * the order they are registered, until the first one returns an non-empty
 * result. If no strategy is present, or none returns a non-empty result, the
 * request is denied.
 */
@Singleton
public class DefaultAuthorizationManager implements AuthorizationManager {

    Multimap<Tripple<Class<? extends Principal>, Class<? extends Operation>, Class<? extends Environment>>, AuthorizationStrategy<?, ?, ?>> strategies = MultimapBuilder
            .hashKeys().arrayListValues().build();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void register(AuthorizationStrategy<?, ?, ?> strategy) {
        TypeToken<? extends AuthorizationStrategy> t = TypeToken.of(strategy
                .getClass());
        Class subject = t.resolveType(
                AuthorizationStrategy.class.getTypeParameters()[0])
                .getRawType();
        Class operation = t.resolveType(
                AuthorizationStrategy.class.getTypeParameters()[1])
                .getRawType();
        Class environment = t.resolveType(
                AuthorizationStrategy.class.getTypeParameters()[2])
                .getRawType();
        register(strategy, subject, operation, environment);
    }

    public <TSubject extends Principal, TOperation extends Operation, TEnvironment extends Environment> void register(
            AuthorizationStrategy<TSubject, TOperation, TEnvironment> strategy,
            Class<TSubject> subject, Class<TOperation> operation,
            Class<TEnvironment> environment) {
        strategies.put(Tripple.of(subject, operation, environment), strategy);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean isGranted(AuthorizationRequest<?, ?, ?> request) {
        for (AuthorizationStrategy strategy : strategies.get((Tripple) Tripple
                .of(request.getSubject().getClass(), request.getOperation()
                        .getClass(), request.getEnvironment().getClass()))) {
            Optional<Boolean> granted = strategy.isGranted(request);
            if (granted.isPresent())
                return granted.get();
        }
        return false;
    }

    /**
     * Check if an {@link AuthorizationRequest} is granted. If the function
     * returns without error, the request was granted.
     * 
     * @throws AuthorizationDeniedException
     *             if the request was not granted
     */
    @Override
    public void checkGranted(AuthorizationRequest<?, ?, ?> request) {
        if (!isGranted(request))
            throw new AuthorizationDeniedException(request);
    }
}
