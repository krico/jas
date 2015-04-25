var util = require('../lib/util');

describe('Sign In with Email', function () {
    var AuthenticatePage = require('./authenticate.page');
    var credentials;
    beforeEach(function(){
        util.credentials('admin').then(function(c){
            credentials = c;
        })
    });

    it('should authenticate as admin and then logout', function () {
        var page = new AuthenticatePage();
        page.go();
        expect(page.isLoggedIn()).not.toBeTruthy();
        expect(page.signInWithEmail(credentials)).toEqual(credentials.user);
        expect(page.isLoggedIn()).toBeTruthy();
        expect(page.getAuthName()).toEqual(credentials.user);
        expect(page.logout()).toBeTruthy();
        expect(page.isLoggedIn()).not.toBeTruthy();
    });
});