var util = require('../lib/util');

describe('Sign In with Facebook', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials = util.credentials('facebook');

    it('should authenticate using facebook', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        page.signInWithFacebook(util.credentials('facebook'));
        expect(page.isLoggedIn()).toBeTruthy();
    });

    it('should logout after authenticating', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).toBeTruthy();
        page.logout();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});