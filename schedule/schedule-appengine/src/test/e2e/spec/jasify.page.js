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
    return element(by.tagName('nav')).all(by.linkText(linkText)).first();
};

module.exports = JasifyPage;
