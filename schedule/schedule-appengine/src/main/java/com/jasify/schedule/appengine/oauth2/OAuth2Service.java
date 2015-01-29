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
     * @param baseUrl  The base url for the site (e.g. http://bla.com)
     * @param provider we want to authenticate with
     * @param state    to be persisted and used after the redirection
     * @return the url that we should redirect the user to authenticate.
     */
    GenericUrl createCodeRequestUrl(GenericUrl baseUrl, OAuth2ProviderEnum provider, @Nonnull Serializable state);

    /**
     * Once a user gets redirected to the authorization url generated with {@link #createCodeRequestUrl} and accepts or
     * cancels, his browser is redirected back to us (with the URL we supplied as a redirect url).  With the information
     * contained in the url that we get called with (<code>codeRequestResponseUrl</code>) we can now go
     * to the provider and request a user token.
     *
     * @param codeRequestResponseUrl the full callback url including query parameters
     * @return the token received from the provider
     * @throws OAuth2Exception in case any part of the operation fails.  Usually this will be one of the subclasses
     * (e.g. {@link com.jasify.schedule.appengine.oauth2.OAuth2Exception.MissingStateException}
     */
    OAuth2UserToken fetchUserToken(GenericUrl codeRequestResponseUrl) throws OAuth2Exception;
}
