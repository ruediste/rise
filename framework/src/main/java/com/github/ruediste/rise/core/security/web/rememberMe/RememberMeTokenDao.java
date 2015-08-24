package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Principal;

public interface RememberMeTokenDao {
    RememberMeToken loadToken(long id);

    /**
     * Store the given subject and token. The id of the provided token is not
     * taken into account.
     *
     * @return the updated token with the id set
     */
    RememberMeToken newToken(RememberMeToken token, Principal principal);

    void updateToken(RememberMeToken token);

    Principal loadPrincipal(long id);

    void delete(long id);

}