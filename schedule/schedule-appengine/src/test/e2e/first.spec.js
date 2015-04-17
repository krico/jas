function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

describe('Jasify Schedule', function () {
    var username = 'protractor_dspkqpy_user@tfbnw.net';
    var pass = 'protractor';
    it('should login with facebook', function () {

        browser.manage().deleteAllCookies();

        browser.get('http://localhost:8080/');


        expect(browser.getTitle()).toEqual('Jasify Schedule');

        element(by.linkText('Create Account')).click();

        browser.waitForAngular();

        var modalTitle = element(by.css('.modal-title'));
        var modalTitleText = modalTitle.getText();
        expect(modalTitleText).toEqual('Create Account');

        element(by.partialButtonText('Create Account with Facebook')).click().then(function () {
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