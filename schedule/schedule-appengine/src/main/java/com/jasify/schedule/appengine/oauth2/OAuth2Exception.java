package com.jasify.schedule.appengine.oauth2;

/**
 * @author krico
 * @since 29/01/15.
 */
public class OAuth2Exception extends Exception {
    public OAuth2Exception() {
    }

    public OAuth2Exception(Throwable cause) {
        super(cause);
    }

    public OAuth2Exception(String message) {
        super(message);
    }

    public static class CodeResponseException extends OAuth2Exception {
        public CodeResponseException(String message) {
            super(message);
        }
    }

    public static class MissingStateException extends OAuth2Exception {
    }

    public static class InconsistentStateException extends OAuth2Exception {
    }

    public static class BadProviderException extends OAuth2Exception {
    }

    public static class TokenRequestException extends OAuth2Exception {
        public TokenRequestException(String message) {
            super(message);
        }

        public TokenRequestException(Throwable cause) {
            super(cause);
        }
    }
}
