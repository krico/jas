package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiCacheControl;
import com.google.api.server.spi.config.ApiFrontendLimits;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author krico
 * @since 29/03/15.
 */
public class JasifyApiTest {
    private static final Logger log = LoggerFactory.getLogger(JasifyApiTest.class);

    @Test
    public void testApiAnnotations() {
        Map<String, String> apiSignature = new TreeMap<>();

        Reflections reflections = new Reflections("com.jasify.schedule.appengine.spi");
        Set<Class<?>> apiClasses = reflections.getTypesAnnotatedWith(Api.class);
        int apiClassCount = apiClasses.size();
        log.info("Detected {} classes annotated with @Api", apiClassCount);
        for (Class<?> apiClass : apiClasses) {
            StringBuilder signature = new StringBuilder("@Api");
            Api api = apiClass.getAnnotation(Api.class);
            assertNotNull(api);

            signature.append("\n\t").append("Root")
                    .append('{').append(api.root()).append('}');

            signature.append("\n\t").append("Name")
                    .append('{').append(api.name()).append('}');

            signature.append("\n\t").append("CanonicalName")
                    .append('{').append(api.canonicalName()).append('}');

            signature.append("\n\t").append("Version")
                    .append('{').append(api.version()).append('}');

            signature.append("\n\t").append("Tittle")
                    .append('{').append(api.title()).append('}');

            signature.append("\n\t").append("Description")
                    .append('{').append(api.description()).append('}');

            signature.append("\n\t").append("DocumentationLink")
                    .append('{').append(api.documentationLink()).append('}');

            signature.append("\n\t").append("BackendRoot")
                    .append('{').append(api.backendRoot()).append('}');

            append(signature.append("\n\t").append("Auth"), api.auth());

            append(signature.append("\n\t").append("FrontendLimits"), api.frontendLimits());

            append(signature.append("\n\t").append("CacheControl"), api.cacheControl());

            signature.append("\n\t").append("AuthLevel")
                    .append('{').append(api.authLevel()).append('}');

            signature.append("\n\t").append("Scopes")
                    .append('{').append(Arrays.toString(api.scopes())).append('}');

            signature.append("\n\t").append("Audiences")
                    .append('{').append(Arrays.toString(api.audiences())).append('}');

            signature.append("\n\t").append("ClientIds")
                    .append('{').append(Arrays.toString(api.clientIds())).append('}');

            signature.append("\n\t").append("Authenticators")
                    .append('{').append(Arrays.toString(api.authenticators())).append('}');

            signature.append("\n\t").append("PeerAuthenticators")
                    .append('{').append(Arrays.toString(api.peerAuthenticators())).append('}');

            signature.append("\n\t").append("Abstract")
                    .append('{').append(api.isAbstract()).append('}');

            signature.append("\n\t").append("DefaultVersion")
                    .append('{').append(api.defaultVersion()).append('}');

            signature.append("\n\t").append("Resource")
                    .append('{').append(api.resource()).append('}');

            signature.append("\n\t").append("Transformers")
                    .append('{').append(Arrays.toString(api.transformers())).append('}');

            signature.append("\n\t").append("UseDatastoreForAdditionalConfig")
                    .append('{').append(api.useDatastoreForAdditionalConfig()).append('}');

            signature.append("\n\t").append("Namespace")
                    .append('{').append(api.namespace()).append('}');


            apiSignature.put(apiClass.getName(), signature.toString());
            log.debug("{} -> {}", apiClass.getName(), signature);
        }

        List<String> names = new ArrayList<>(apiSignature.keySet());
        for (int i = 1; i < apiClassCount; ++i) {
            String base = apiSignature.get(names.get(0));
            assertEquals("If all @Api headers are not the same, the endpoint will not work\n " +
                    names.get(0) + "\n " + names.get(i), base, apiSignature.get(names.get(i)));
        }
    }

    private StringBuilder append(StringBuilder signature, ApiCacheControl cacheControl) {
        signature.append("\n\t\t").append("Type")
                .append('{').append(cacheControl.type()).append('}');

        signature.append("\n\t\t").append("MaxAge")
                .append('{').append(cacheControl.maxAge()).append('}');

        return signature;
    }

    private StringBuilder append(StringBuilder signature, ApiFrontendLimits frontendLimits) {
        signature.append("\n\t\t").append("Rules")
                .append('{').append(Arrays.toString(frontendLimits.rules())).append('}');

        signature.append("\n\t\t").append("UnregisteredDaily")
                .append('{').append(frontendLimits.unregisteredDaily()).append('}');

        signature.append("\n\t\t").append("UnregisteredQps")
                .append('{').append(frontendLimits.unregisteredQps()).append('}');

        signature.append("\n\t\t").append("UnregisteredUserQps")
                .append('{').append(frontendLimits.unregisteredUserQps()).append('}');

        return signature;
    }

    private StringBuilder append(StringBuilder signature, ApiAuth auth) {

        signature.append("\n\t\t").append("AllowCookieAuth")
                .append('{').append(auth.allowCookieAuth()).append('}');

        signature.append("\n\t\t").append("BlockedRegions")
                .append('{').append(Arrays.toString(auth.blockedRegions())).append('}');

        return signature;

    }
}
