package com.github.ruediste.rise.core.security.web.rememberMe;

import com.github.ruediste.rise.core.security.Subject;

public interface RememberMeTokenDao {
    RememberMeToken loadToken(long id);

    void newToken(RememberMeToken token, Subject subject);

    void updateToken(RememberMeToken token);

    Subject loadSubject(long id);

}