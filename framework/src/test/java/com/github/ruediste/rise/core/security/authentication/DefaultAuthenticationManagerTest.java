package com.github.ruediste.rise.core.security.authentication;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.security.Subject;

public class DefaultAuthenticationManagerTest {

    private DefaultAuthenticationManager mgr;

    @Before
    public void before() {
        mgr = new DefaultAuthenticationManager();
        mgr.addProvider(new InMemoryAuthenticationProvider<Subject>().with(
                "foo", "bar", null));
        mgr.addProvider(new InMemoryAuthenticationProvider<Subject>().with(
                "foo2", "bar2", null));
    }

    @Test
    public void testAuthenticate() throws Exception {
        mgr.authenticate(new UsernamePasswordAuthenticationRequest("foo", "bar"));
    }

    @Test
    public void testAuthenticateUserNotFound() throws Exception {
        AuthenticationResult result = mgr
                .authenticate(new UsernamePasswordAuthenticationRequest(
                        "Hello", "bar"));
        assertEquals(2, result.getFailures().size());
    }

    @Test
    public void testAuthenticate2nd() throws Exception {
        mgr.authenticate(new UsernamePasswordAuthenticationRequest("foo2",
                "bar2"));
    }
}
