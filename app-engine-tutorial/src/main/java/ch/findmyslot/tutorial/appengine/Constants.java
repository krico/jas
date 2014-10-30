package ch.findmyslot.tutorial.appengine;

/**
 * Created by krico on 29/10/14.
 */
public interface Constants {
    interface OpenID {

        interface Credentials {
            String ClientID = "618453286115-fmc476v3m0ca3pk9q5edmug1qkkf0q70.apps.googleusercontent.com";
            String ClientSecret = "bfOAFLpYUJM5kl2E0F8R3zoB"; //todo: REGENERATE THIS and not keep it in code
            String ApplicationName = "krico-test";
        }

        /*
        example:
        https://accounts.google.com/o/oauth2/auth?client_id=618453286115-fmc476v3m0ca3pk9q5edmug1qkkf0q70.apps.googleusercontent.com&response_type=code&scope=openid%20email&redirect_uri=https://krico-test.appspot.com/openid-callback&state=test
        */
        interface Fields {
            String ClientId = "client_id";
            String ClientSecret = "client_secret";
            String AuthorizationEndpoint = "authorization_endpoint";
            String TokenEndpoint = "token_endpoint";

        }
    }


    //TODO: These should come from discovery
    interface Endpoint {
        interface Authorization {
            String Google = "https://accounts.google.com/o/oauth2/auth";
        }

        interface Token {
            String Google = "https://accounts.google.com/o/oauth2/token";
        }
    }

    interface Discovery {
        interface Url {
            String Google = "https://accounts.google.com/.well-known/openid-configuration";
        }
    }

    interface Session {
        String OpenIDState = "OpenID.State";
    }

}
