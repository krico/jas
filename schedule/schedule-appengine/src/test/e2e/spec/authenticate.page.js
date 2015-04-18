var JasifyPage = require(__dirname + '/jasify.page');
var inherits = require('util').inherits;

function AuthenticatePage() {
    JasifyPage.call(this);
    this.signInMenu = this.menuItem('Sign In');
    this.logOutMenu = this.menuItem('log out');
    this.logOutConfirmation = element(by.css('.glyphicon-log-out'));
    this.signInWithFacebookButton = element(by.partialButtonText('Sign In with Facebook'));
    this.authStatus = element(by.id('auth.status'));
    this.currentUser = element.all(by.binding('currentUser.name')).first();
}

inherits(AuthenticatePage, JasifyPage);

AuthenticatePage.prototype.logout = function (credentials) {
    expect(this.logOutMenu.isPresent()).toBeTruthy();
    this.logOutMenu.click();
    expect(this.logOutConfirmation.isPresent()).toBeTruthy();
};

AuthenticatePage.prototype.signInWithFacebook = function (credentials) {

    expect(this.signInMenu.isPresent()).toBeTruthy();
    this.signInMenu.click();

    expect(this.signInWithFacebookButton.isPresent()).toBeTruthy();
    browser.ignoreSynchronization = true;
    this.signInWithFacebookButton.click();


    var fbPageLoginButton = element(by.id('loginbutton'));

    browser.wait(this.EC.presenceOf(fbPageLoginButton), this.externalTimeout);

    expect(element(by.id('email')).isPresent()).toBeTruthy();
    expect(element(by.id('pass')).isPresent()).toBeTruthy();
    element(by.id('email')).sendKeys(credentials.user);
    element(by.id('pass')).sendKeys(credentials.pass);

    fbPageLoginButton.click().then(function () {
        browser.ignoreSynchronization = true;
    });

    browser.wait(this.EC.textToBePresentInElement(this.currentUser, credentials.user), this.externalTimeout);
};
var COUNTER = 0;
AuthenticatePage.prototype.isLoggedIn = function () {
    var that = this;
    browser.waitForAngular();

    return this.authStatus.isPresent().then(function (present) {
        if (!present) {
            return false;
        }
        return that.authStatus.getAttribute('value').then(function (value) {
            return value == 'authenticated';
        });
    });
};

module.exports = AuthenticatePage;