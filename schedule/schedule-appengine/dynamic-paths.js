var xml2json = require('xml-to-json');

function mavenVersion() {
    //This is so ugly, I'm embarrassed ;-)
    var gotIt = {err: null, result: null};

    xml2json({
        input: __dirname + '/pom.xml',
        output: null
    }, function (err, result) {
        gotIt.err = err;
        gotIt.result = result;
    });
    var uvrun = require("uvrun");
    while (gotIt.err === null && gotIt.result === null)
        uvrun.runOnce();

    if (gotIt.err)
        throw gotIt.err;

    return gotIt.result.project.version;
}
var paths = require(__dirname + '/paths.json');
paths.projectVersion = mavenVersion();
paths.build = paths.buildTemplate.replace('@VERSION@', paths.projectVersion);
module.exports = paths;
