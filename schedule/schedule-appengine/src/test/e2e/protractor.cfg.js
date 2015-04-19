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
    capabilities: {
        //ref: https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
        name: 'Jasify(chrome)',
        browserName: 'chrome',
        maxInstances: 1,
        shardTestFiles: false
    },
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
    config.capabilities['tunnel-identifier'] = process.env.TRAVIS_JOB_NUMBER;
    config.capabilities['build'] = process.env.TRAVIS_BUILD_NUMBER;
    config.capabilities['tags'] = [process.env.TRAVIS_BRANCH];
    config.capabilities['public'] = 'public restricted';
}

exports.config = config;