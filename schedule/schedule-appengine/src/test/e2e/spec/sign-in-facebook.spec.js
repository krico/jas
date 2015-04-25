var util = require('../lib/util');

describe('Sign In with Facebook', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials;
    beforeEach(function(){
        util.credentials('facebook').then(function(c){
            credentials = c;
        })
    });

    it('should authenticate using facebook', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        page.signInWithFacebook(credentials);
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user)
    });

    it('should logout after authenticating', function () {
        var page = new AuthenticatePage();
        page.go();
        browser.sleep(500);
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user);
        page.logout();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});