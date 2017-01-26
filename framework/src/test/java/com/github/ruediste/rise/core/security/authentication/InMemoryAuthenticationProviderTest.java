package com.github.ruediste.rise.core.security.authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.rise.core.security.Principal;

public class InMemoryAuthenticationProviderTest {

    @Test
    public void testAuthenticate() throws Exception {
        InMemoryAuthenticationProvider<Principal> provider = new InMemoryAuthenticationProvider<>().with("foo", "bar",
                null);
        assertTrue(provider.authenticate(new AuthenticationRequestUsernamePassword("foo", "bar")).isSuccess());
        assertFalse(provider.authenticate(new AuthenticationRequestUsernamePassword("foo", "world")).isSuccess());
        assertTrue(provider.authenticate(new AuthenticationRequestUsernamePassword("foo", "world")).getFailures()
                .get(0) instanceof PasswordMismatchAuthenticationFailure);
        assertTrue(provider.authenticate(new AuthenticationRequestUsernamePassword("hello", "foo")).getFailures()
                .get(0) instanceof UserNameNotFoundAuthenticationFailure);
    }
}
