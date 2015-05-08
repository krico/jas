var paths = require(__dirname + '/paths.json');
paths.projectVersion = '1.0.3-SNAPSHOT';
paths.build = paths.buildTemplate.replace('@VERSION@', paths.projectVersion);
module.exports = paths;