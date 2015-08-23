package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Subject;

public interface RememberMeTokenDao {
    RememberMeToken loadToken(long id);

    /**
     * Store the given subject and token. The id of the provided token is not
     * taken into account.
     *
     * @return the updated token with the id set
     */
    RememberMeToken newToken(RememberMeToken token, Subject subject);

    void updateToken(RememberMeToken token);

    Subject loadSubject(long id);

}