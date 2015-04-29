var JasifyPage = require(__dirname + '/jasify.page');
var inherits = require('util').inherits;

function AuthenticatePage() {
    JasifyPage.call(this);
    this.signInMenu = this.menuItem('Sign In');
    this.logOutMenu = this.menuItem('log out');
    this.logOutConfirmation = element(by.css('.glyphicon-log-out'));
    this.signInWithFacebookButton = element(by.partialButtonText('Sign In with Facebook'));
    this.signInWithEmailButton = element(by.partialButtonText('Sign In with Email'));
}

inherits(AuthenticatePage, JasifyPage);

AuthenticatePage.prototype.logout = function (credentials) {
    var that = this;
    return this.logOutMenu.isDisplayed()
        .then(function (displayed) {
            if (!displayed) throw 'log out menu not displayed';
            return that.logOutMenu.click();
        })
        .then(function () {
            browser.sleep(1000);
            return that.logOutConfirmation.isDisplayed();
        });
};

AuthenticatePage.prototype.signInWithEmail = function (credentials) {
    var that = this;
    return this.signInMenu.isPresent()
        .then(clickMenu)
        .then(clickButton)
        .then(submitForm)
        .then(function () {
            browser.sleep(1000);
            return that.getAuthName();
        });

    function clickMenu(p) {
        if (!p) throw 'signIn menu not there';
        return that.signInMenu.click();
    }

    function clickButton() {
        return that.signInWithEmailButton.isPresent().then(function (p) {
            if (!p) throw 'signInWithEmailButton not present';
            return that.signInWithEmailButton.click();
        });
    }

    function submitForm() {
        var userField = element(by.model('vm.user.email'));
        var passField = element(by.model('vm.user.password'));
        var signInButton = element(by.css('[ng-click="vm.signIn($close)"]'));
        return signInButton.isDisplayed().then(
            function (d) {
                if (!d) throw 'signInButton not displayed';
                userField.sendKeys(credentials.user);
                passField.sendKeys(credentials.pass);
                return signInButton.click();
            }
        );

    }
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

    browser.wait(this.EC.textToBePresentInElementValue(this.authName, credentials.user), this.externalTimeout);
};

AuthenticatePage.prototype.isLoggedIn = function () {
    var that = this;
    return browser.waitForAngular()
        .then(function () {
            return that.authStatus.isPresent()
        })
        .then(function (p) {
            if (!p) throw 'Auth status not present';
            return that.getAuthStatus();
        })
        .then(function (status) {
            return status == 'authenticated';
        });
};

module.exports = AuthenticatePage;