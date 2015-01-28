package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.http.GenericUrl;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author krico
 * @since 27/01/15.
 */
public interface OAuth2Service {
    /**
     * Generates a url that can be used to redirect a client to authenticate with <code>provider</code>.
     * The <code>state</code> will be persisted and linked so that after the authentication completes and the users
     * gets redirected back, we can restore that information.
     *
     * @param baseUrl The base url for the site (e.g. http://bla.com)
     * @param provider we want to authenticate with
     * @param state    to be persisted and used after the redirection
     * @return the url that we should redirect the user to authenticate.
     */
    GenericUrl createCodeRequestUrl(GenericUrl baseUrl, OAuth2ProviderConfig.ProviderEnum provider, @Nonnull Serializable state);
}
