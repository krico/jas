/**
 * Base class for jasify pages.
 * Boilerplate for pages that want to inherit would look like
 *
 * <pre>
 * var JasifyPage = require(__dirname + '/jasify.page');
 * var inherits = require('util').inherits;
 *
 * function SomePage() {
 *   JasifyPage.call(this);
 * }
 *
 * inherits(SomePage, JasifyPage);
 *
 * SomePage.prototype.someMethod = function() {
 *   //some code
 * }
 *
 * module.exports = AuthenticatePage;
 * </pre>
 * @constructor
 */
function JasifyPage(path) {
    this.path = path || '/';
    this.authStatus = element(by.id('auth.status'));
    this.authName = element(by.id('auth.name'));
    this.authId = element(by.id('auth.id'));
    this.EC = protractor.ExpectedConditions;
    this.externalTimeout = browser.params.externalTimeout;
}

JasifyPage.prototype.go = function () {
    return browser.get(this.path);
};

JasifyPage.prototype.reload = function () {
    return browser.refresh();
};

JasifyPage.prototype.title = function () {
    return browser.getTitle();
};

JasifyPage.prototype.menuItem = function (linkText) {
    return element(by.tagName('li')).all(by.linkText(linkText)).first();
};

JasifyPage.prototype.menuButton = function (linkText) {
    return element(by.tagName('md-button')).all(by.partialButtonText(linkText)).first();
};

JasifyPage.prototype.getAuthStatus = function () {
    return this.authStatus.getAttribute('value');
};

JasifyPage.prototype.getAuthName = function () {
    return this.authName.getAttribute('value');
};

JasifyPage.prototype.getAuthId = function () {
    return this.authId.getAttribute('value');
};

module.exports = JasifyPage;
