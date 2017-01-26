package com.github.ruediste.rise.core.security.authentication.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.InMemoryAuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.AuthenticationRequestUsernamePassword;

public class DefaultAuthenticationManagerTest {

    private DefaultAuthenticationManager mgr;

    @Before
    public void before() {
        mgr = new DefaultAuthenticationManager();
        mgr.addProvider(new InMemoryAuthenticationProvider<Principal>().with("foo", "bar", null));
        mgr.addProvider(new InMemoryAuthenticationProvider<Principal>().with("foo2", "bar2", null));
    }

    @Test
    public void testAuthenticate() throws Exception {
        mgr.authenticate(new AuthenticationRequestUsernamePassword("foo", "bar"));
    }

    @Test
    public void testAuthenticateUserNotFound() throws Exception {
        AuthenticationResult result = mgr.authenticate(new AuthenticationRequestUsernamePassword("Hello", "bar"));
        assertEquals(1, result.getFailures().size());
    }

    @Test
    public void testAuthenticate2nd() throws Exception {
        mgr.authenticate(new AuthenticationRequestUsernamePassword("foo2", "bar2"));
    }
}
