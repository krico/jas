var BasePage = require(__dirname + '/base.page');
var util = require('util');

function HomePage(path) {
    BasePage.call(this);
    this.path = path || '/#home';
    this.signInMenuButton = element.all(by.buttonText('Sign In')).get(0);
    this.signInButton = element.all(by.buttonText('Sign In')).get(1);
}

util.inherits(HomePage, BasePage);

module.exports = HomePage;