package com.github.ruediste.rise.nonReloadable.persistence;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.Session;

public class CompositeSessionCustomizer implements SessionCustomizer {

    public static Iterable<? extends SessionCustomizer> customizers;

    @Override
    public void customize(Session session) throws Exception {
        for (SessionCustomizer customizer : customizers) {
            customizer.customize(session);
        }
    }
}
