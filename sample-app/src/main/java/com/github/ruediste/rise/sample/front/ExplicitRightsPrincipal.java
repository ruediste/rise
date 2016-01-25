package com.github.ruediste.rise.sample.front;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.ruediste.rise.core.security.Principal;

/**
 * Principal directly containing the rights granted to the principal. Typically,
 * you will only store an ID and determine the allowed rights in a different
 * way, perhaps using some role system.
 */
public class ExplicitRightsPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1L;
    public Set<SampleRight> grantedRights = new HashSet<>();

    public ExplicitRightsPrincipal(SampleRight... rights) {
        grantedRights.addAll(Arrays.asList(rights));
    }
}
