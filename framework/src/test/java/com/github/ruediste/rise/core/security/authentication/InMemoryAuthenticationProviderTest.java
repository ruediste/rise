package com.github.ruediste.rise.core.security.authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.rise.core.security.Principal;

public class InMemoryAuthenticationProviderTest {

    @Test
    public void testTryAuthenticate() throws Exception {
        InMemoryAuthenticationProvider<Principal> provider = new InMemoryAuthenticationProvider<>()
                .with("foo", "bar", null);
        assertTrue(provider
                .tryAuthenticate(
                        new UsernamePasswordAuthenticationRequest("foo", "bar"))
                .isSuccess());
        assertFalse(provider.tryAuthenticate(
                new UsernamePasswordAuthenticationRequest("foo", "world"))
                .isSuccess());
        assertTrue(provider
                .tryAuthenticate(new UsernamePasswordAuthenticationRequest(
                        "foo", "world"))
                .getFailures()
                .get(0) instanceof PasswordMismatchAuthenticationFailure);
        assertTrue(provider
                .tryAuthenticate(new UsernamePasswordAuthenticationRequest(
                        "hello", "foo"))
                .getFailures()
                .get(0) instanceof UserNameNotFoundAuthenticationFailure);
    }
}
