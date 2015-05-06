var util = require('util');
var BasePage = require(__dirname + '/base.page');

function AuthenticatePage() {
    BasePage.call(this);
    this.signInWithEmail = {
        button: element(by.buttonText('Sign In with Email')),
        emailField: element(by.model('vm.user.email')),
        passwordField: element(by.model('vm.user.password')),
        signIn: element(by.css('button[type="submit"]'))
    };
}

util.inherits(AuthenticatePage, BasePage);

AuthenticatePage.prototype.clickSignInWithEmail = function () {
    return this.waitAndClick(this.signInWithEmail.button);
};

AuthenticatePage.prototype.submitSignInWithEmail = function (cred) {
    var form = this.signInWithEmail;
    return this.waitFor(form.passwordField).then(function () {
        form.emailField.sendKeys(cred.user);
        form.passwordField.sendKeys(cred.pass);
        return form.signIn.click();
    });
};

module.exports = AuthenticatePage;