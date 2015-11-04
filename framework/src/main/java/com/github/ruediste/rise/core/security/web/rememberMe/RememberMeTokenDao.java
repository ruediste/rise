package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Principal;

public interface RememberMeTokenDao {
    /**
     * Load the token with the given Id
     */
    RememberMeToken loadToken(long id);

    /**
     * Store the given subject and token. The id of the provided token is not
     * taken into account.
     *
     * @return the updated token with the id set
     */
    RememberMeToken newToken(RememberMeToken token, Principal principal);

    /**
     * Update a modified token
     */
    void updateToken(RememberMeToken token);

    /**
     * Load the principal for the given token id
     */
    Principal loadPrincipal(long id);

    /**
     * Delete the token with the given id
     */
    void delete(long id);

}