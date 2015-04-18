var util = require('../lib/util');

describe('Sign In with Email', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials = util.credentials('admin');

    it('should authenticate as admin', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        expect(page.signInWithEmail(credentials)).toEqual(credentials.user);
        expect(page.isLoggedIn()).toBeTruthy();
    });

    it('should logout after authenticating as admin', function () {
        var page = new AuthenticatePage();
        page.go();
        browser.sleep(500); //we need to wait for the API, protractor doesn't know about google endpoints :-(
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user);
        expect(page.logout()).toBeTruthy();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});