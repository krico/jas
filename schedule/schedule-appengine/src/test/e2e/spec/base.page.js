/**
 * Base class for jasify pages.
 * Boilerplate for pages that want to inherit would look like
 *
 * <pre>
 * var util = require('util');
 * var BasePage = require(__dirname + '/base.page');
 *
 * function SomePage() {
 *   BasePage.call(this);
 * }
 *
 * util.inherits(SomePage, BasePage);
 *
 * SomePage.prototype.someMethod = function() {
 *   //some code
 * }
 *
 * module.exports = SomePage;
 * </pre>
 * @constructor
 */
function BasePage(path) {
    this.path = path || '/';
    this.authStatus = element(by.id('auth.status'));
    this.authName = element(by.id('auth.name'));
    this.authId = element(by.id('auth.id'));
}

BasePage.prototype.go = function (path) {
    return browser.get(path || this.path);
};

BasePage.prototype.getAuthStatus = function () {
    return this.authStatus.getAttribute('value');
};

BasePage.prototype.waitFor = function (el) {
    return browser.wait(function () {
        return el.isPresent();
    }).then(function () {
        return el;
    });
};

BasePage.prototype.waitAndClick = function (el) {
    return this.waitFor(el).then(function (b) {
        return b.click();
    });
};

module.exports = BasePage;
