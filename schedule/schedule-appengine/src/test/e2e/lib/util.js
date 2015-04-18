var fs = require('fs');
var assert = require('assert');

var util = module.exports = {
    screenShot: screenShot,
    credentials: credentials
};

function screenShot(name) {
    function writeScreenShot(data, filename) {
        var stream = fs.createWriteStream(filename);

        stream.write(new Buffer(data, 'base64'));
        stream.end();
    }

    browser.takeScreenshot().then(function (png) {
        writeScreenShot(png, name + '.png');
    });
}

function credentials(type) {
    assert(typeof(browser.params.logins[type]) == 'object');
    return browser.params.logins[type];
}