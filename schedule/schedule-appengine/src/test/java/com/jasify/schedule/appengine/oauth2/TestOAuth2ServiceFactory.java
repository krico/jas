package com.jasify.schedule.appengine.oauth2;

import org.easymock.EasyMock;

/**
 * @author krico
 * @since 27/01/15.
 */
public class TestOAuth2ServiceFactory extends OAuth2ServiceFactory {
    private OAuth2Service oAuth2ServiceMock;

    public void setUp() {
        oAuth2ServiceMock = EasyMock.createMock(OAuth2Service.class);
        setInstance(oAuth2ServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        if (oAuth2ServiceMock != null)
            EasyMock.verify(oAuth2ServiceMock);
        oAuth2ServiceMock = null;
    }

    public void replay() {
        EasyMock.replay(oAuth2ServiceMock);
    }
}
