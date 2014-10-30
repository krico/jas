package ch.findmyslot.tutorial.appengine;

/**
 * Created by krico on 29/10/14.
 */
public interface Constants {
    interface OpenID {
        String ClientID = "618453286115-fmc476v3m0ca3pk9q5edmug1qkkf0q70.apps.googleusercontent.com";
        String ApplicationName = "krico-test";

        /*
        example:
        https://accounts.google.com/o/oauth2/auth?client_id=618453286115-fmc476v3m0ca3pk9q5edmug1qkkf0q70.apps.googleusercontent.com&response_type=code&scope=openid%20email&redirect_uri=https://krico-test.appspot.com/openid-callback&state=test
        */
    }

    interface AuthorizationEndpoint {
        //TODO: This should come from discovery
        String Google = "https://accounts.google.com/o/oauth2/auth";
    }

    interface Discovery {
        interface Endpoint {
            String Authorization = "authorization_endpoint";
            String Token = "token_endpoint";
        }

        interface Url {
            String Google = "https://accounts.google.com/.well-known/openid-configuration";
        }
    }

    interface Session {
        String OpenIDState = "OpenID.State";
    }

}
