package com.jasify.schedule.appengine.model.users;

import org.easymock.EasyMock;

/**
 * @author krico
 * @since 08/01/15.
 */
public class TestUserServiceFactory extends UserServiceFactory {

    private UserService userServiceMock;

    public void setUp() {
        userServiceMock = EasyMock.createMock(UserService.class);
        setInstance(userServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        if (userServiceMock != null)
            EasyMock.verify(userServiceMock);
        userServiceMock = null;
    }

    public UserService getUserServiceMock() {
        return userServiceMock;
    }

    public void replay() {
        EasyMock.replay(userServiceMock);
    }
}
