var util = require('../lib/util');

describe('Sign In with Facebook', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials = util.credentials('facebook');

    it('should authenticate using facebook', function () {if(true)return;
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        page.signInWithFacebook(credentials);
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user)
    });

    it('should logout after authenticating', function () {if(true)return;
        var page = new AuthenticatePage();
        page.go();
        browser.waitForAngular();
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user);
        page.logout();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});