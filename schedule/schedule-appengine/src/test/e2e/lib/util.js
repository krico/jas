var fs = require('fs');
var util = module.exports = {
    screenShot: screenShot
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