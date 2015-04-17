exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl: 'http://localhost:8080',
    suites: {
        full: 'spec/*.spec.js'
    },
    framework: 'jasmine2',
    params: {
        logins: {
            facebook: {
                user: 'protractor_dspkqpy_user@tfbnw.net',
                pass: 'protractor'
            }
        }
    }
};