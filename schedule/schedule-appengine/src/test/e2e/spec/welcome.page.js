var util = require('util');
var BasePage = require(__dirname + '/base.page');

function WelcomePage(path) {
    BasePage.call(this);
    this.path = path || '/#/';
    this.welcomeText = element.all(by.binding('currentUser.name')).first();
    this.logoutButton = element.all(by.linkText('Log out')).first();
}

util.inherits(WelcomePage, BasePage);

WelcomePage.prototype.getWelcomeText = function () {
    return this.waitFor(this.welcomeText).then(function (e) {
        return e.getText();
    });
};

WelcomePage.prototype.clickLogout = function () {
    return this.waitAndClick(this.logoutButton);
};

module.exports = WelcomePage;