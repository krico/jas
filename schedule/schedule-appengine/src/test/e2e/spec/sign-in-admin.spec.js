var util = require('../lib/util');

describe('Sign In with Email', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials = util.credentials('admin');

    it('should authenticate as admin', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        page.signInWithEmail(credentials);
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user)
    });

    it('should logout after authenticating as admin', function () {
        var page = new AuthenticatePage();
        page.go();
        browser.waitForAngular();
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user);
        page.logout();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});