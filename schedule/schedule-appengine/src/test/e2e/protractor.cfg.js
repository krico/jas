var config = {
    //seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl: 'http://localhost:8080',
    suites: {
        admin: 'spec/*-admin.spec.js',
        facebook: 'spec/*-facebook.spec.js',
        ci: 'spec/*-admin.spec.js', //run in travis-ci + sauce-labs
        full: 'spec/*.spec.js'
    },
    framework: 'jasmine2',
    params: {
        externalTimeout: 20000,
        logins: {
            facebook: {
                user: 'protractor_dspkqpy_user@tfbnw.net',
                pass: 'protractor'
            },
            admin: {
                user: 'admin',
                pass: 'admin'
            }
        }
    },
    //ref: https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
    maxSessions: 1,
    multiCapabilities: [
        {browserName: 'chrome'}
    ],
    beforeLaunch: function () {
        // protractor not available here
    },
    onPrepare: function () {
        //     jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter(
        //         'outputdir/', true, true));
    }
};

if (process.env.TRAVIS_BUILD_NUMBER) {
    config.sauceUser = process.env.SAUCE_USERNAME;
    config.sauceKey = process.env.SAUCE_ACCESS_KEY;

    // we add some browsers when for sauce labs
    // ref: https://docs.saucelabs.com/reference/platforms-configurator
    config.multiCapabilities.push(
        {browserName: 'internet explorer', platform: 'Windows 8.1', version: '11.0'},
        {browserName: 'internet explorer', platform: 'Windows 8', version: '10.0'},
        {browserName: 'internet explorer', platform: 'Windows 7', version: '9.0'},
        {browserName: 'safari', platform: 'OS X 10.10', version: '8.0'},
        {browserName: 'safari', platform: 'OS X 10.9', version: '7.0'},
        {browserName: 'chrome', platform: 'Linux', version: '26.0'},
        {browserName: 'chrome', platform: 'Linux', version: '41.0'},
        {browserName: 'chrome', platform: 'OS X 10.10', version: '41.0'},
        {browserName: 'chrome', platform: 'Windows 8.1', version: '41.0'},
        {browserName: 'firefox'}
    );

    config.multiCapabilities.forEach(function (value, index) {
        value['tunnel-identifier'] = process.env.TRAVIS_JOB_NUMBER;
        value['build'] = process.env.TRAVIS_BUILD_NUMBER;
        value['tags'] = [process.env.TRAVIS_BRANCH];
        value['public'] = 'public restricted';
    });
}

config.multiCapabilities.forEach(function (value, index) {
    value['name'] = 'Jasify(chrome)';
    value['maxInstances'] = 1;
    value['shardTestFiles'] = false;
});

exports.config = config;