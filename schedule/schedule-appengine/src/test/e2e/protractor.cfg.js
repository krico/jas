exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl: 'http://localhost:8080',
    suites: {
        full: 'spec/*.spec.js'
    },
    framework: 'jasmine2',
    params: {
        externalTimeout: 20000,
        logins: {
            facebook: {
                user: 'protractor_dspkqpy_user@tfbnw.net',
                pass: 'protractor'
            }
        }
    },
    capabilities: {
        //ref: https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
        browserName: 'chrome',
        name: 'Jasify(chrome)'
    },
    beforeLaunch: function () {
        // protractor not available here
    },
    onPrepare: function () {
        //     jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter(
        //         'outputdir/', true, true));
    }
};