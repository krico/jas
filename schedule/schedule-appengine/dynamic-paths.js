var fs = require('fs'),
    xml2js = require('xml2js');

var parser = new xml2js.Parser();
var pomXml = fs.readFileSync(__dirname + '/pom.xml');
var pomJson;

parser.parseString(pomXml, function (err, result) {
    pomJson = result;
});


var paths = require(__dirname + '/paths.json');
paths.projectVersion = pomJson.project.version;
paths.build = paths.buildTemplate.replace('@VERSION@', paths.projectVersion);
module.exports = paths;


