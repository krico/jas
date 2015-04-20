require('require-xml');
var pom = JSON.parse(require('./pom.xml'));
var paths = require(__dirname + '/paths.json');
paths.projectVersion = pom.project.version;
paths.build = paths.buildTemplate.replace('@VERSION@', paths.projectVersion);
module.exports = paths;
