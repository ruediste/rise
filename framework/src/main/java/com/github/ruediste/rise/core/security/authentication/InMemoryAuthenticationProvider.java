package com.github.ruediste.rise.core.security.authentication;

import java.util.HashMap;
import java.util.Objects;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.util.Pair;

/**
 * {@link AuthenticationProvider} with a predefinded set of users, stored in
 * memory
 */
public class InMemoryAuthenticationProvider<T extends Principal>
        implements AuthenticationProvider<UsernamePasswordAuthenticationRequest> {

    public HashMap<String, Pair<String, T>> users = new HashMap<>();

    @Override
    public AuthenticationResult authenticate(UsernamePasswordAuthenticationRequest request) {
        Pair<String, T> pair = users.get(request.getUserName());
        if (pair == null)
            return AuthenticationResult.failure(new UserNameNotFoundAuthenticationFailure(request.getUserName()));

        if (Objects.equals(request.getPassword(), pair.getA())) {
            return AuthenticationResult.success(pair.getB());
        } else
            return AuthenticationResult.failure(new PasswordMismatchAuthenticationFailure());
    }

    public InMemoryAuthenticationProvider<T> with(String userName, String password, T subject) {
        users.put(userName, Pair.of(password, subject));
        return this;
    }
}
