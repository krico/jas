var util = require('../lib/util');

describe('Sign In with Email', function () {
    var HomePage = require('./home.page');
    var AuthenticatePage = require('./authenticate.page');
    var WelcomePage = require('./welcome.page');

    var credentials;
    beforeEach(function (done) {
        util.credentials('admin').then(function (c) {
            credentials = c;
            done();
        });
    });

    it('should login and then logout', function (done) {
        var page = new HomePage();
        page.go();
        expect(page.getAuthStatus()).toEqual('anonymous');
        page.signInMenuButton.click();
        var modal = new AuthenticatePage();
        modal.clickSignInWithEmail();
        modal.submitSignInWithEmail(credentials);

        var welcome = new WelcomePage();
        expect(welcome.getWelcomeText()).toEqual('Hi `' + credentials.user + '`');
        welcome.clickLogout();

        page = new HomePage();
        expect(page.getAuthStatus()).toEqual('anonymous');
        done();
    });
});