var util = require('../lib/util');

describe('Jasify Schedule', function () {

    var username = browser.params.logins.facebook.user;
    var pass = browser.params.logins.facebook.pass;

    it('should login with facebook', function () {

        browser.manage().deleteAllCookies();

        browser.get('/');
        browser.waitForAngular();

        expect(browser.getTitle()).toEqual('Jasify Schedule');

        element(by.linkText('Sign In')).click();

        util.screenShot('home');

        browser.waitForAngular();

        var modalTitle = element(by.css('.modal-title'));
        var modalTitleText = modalTitle.getText();
        expect(modalTitleText).toEqual('Sign In');

        element(by.partialButtonText('Sign In with Facebook')).click().then(function () {
            browser.ignoreSynchronization = true;
            browser.sleep(1000);
            expect(element(by.id('email')).isPresent()).toBeTruthy();
            element(by.id('email')).sendKeys(username);
            element(by.id('pass')).sendKeys(pass);
            element(by.id('loginbutton')).click().then(function () {
                browser.ignoreSynchronization = false;

                browser.waitForAngular();

                var div = element(by.binding('currentUser.name'));

                var EC = protractor.ExpectedConditions;

                browser.wait(EC.textToBePresentInElement(div, username), 10000);
            });
        });


    });
});