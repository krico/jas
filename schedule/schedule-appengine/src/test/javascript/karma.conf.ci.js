var baseConfig = require('./karma.conf.js');

module.exports = function (config) {
    // Load base config
    baseConfig(config, true);

    // Override base config
    config.set({
        singleRun: true,
        autoWatch: false,
        reporters: ['progress', 'junit'],
        junitReporter: {
            outputFile: 'target/surefire-reports/javascript-results.xml',
            suite: ''
        },
        browsers: ['PhantomJS']
    });
};