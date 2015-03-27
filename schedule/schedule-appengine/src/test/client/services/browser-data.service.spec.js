describe('BrowserDataService', function () {
    var BrowserData;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_BrowserData_) {
        BrowserData = _BrowserData_;
    }));

    it('should define defaults', function () {
        expect(BrowserData.DEFAULTS).toBeDefined();
        expect(typeof BrowserData.DEFAULTS).toBe('object');
    });

    it('should return true for getFirstAccess', function () {
        expect(BrowserData.getFirstAccess()).toBe(true);
    });

    it('should return undefined for rememberUser', function () {
        expect(BrowserData.getRememberUser()).not.toBeDefined();
    });

    it('should return false for loggedIn', function () {
        expect(BrowserData.getLoggedIn()).toBe(false);
    });


    /**
     * Since the functions in BrowserData are generated, we can test them once with one property
     * and this works for all
     */
    describe('selectedPropertyChecks', function () {

        it('should return default for getPaymentAcceptRedirect', function () {
            expect(BrowserData.getPaymentAcceptRedirect()).toBe(BrowserData.DEFAULTS.paymentAcceptRedirect);
        });

        it('should know if paymentAcceptRedirect was set or not', function () {
            expect(BrowserData.isPaymentAcceptRedirectSet()).toBe(false);

            BrowserData.setPaymentAcceptRedirect(false);

            expect(BrowserData.isPaymentAcceptRedirectSet()).toBe(true);

            BrowserData.clearPaymentAcceptRedirect();

            expect(BrowserData.isPaymentAcceptRedirectSet()).toBe(false);

        });

        it('should return the value set for getPaymentAcceptRedirect', function () {
            var expected = '/my/redirect';
            BrowserData.setPaymentAcceptRedirect(expected);
            expect(BrowserData.getPaymentAcceptRedirect()).toBe(expected);
        });

        it('should clear the value set for clearPaymentAcceptRedirect', function () {
            var expected = '/my/redirect';
            BrowserData.setPaymentAcceptRedirect(expected);
            expect(BrowserData.getPaymentAcceptRedirect()).toBe(expected);
            BrowserData.clearPaymentAcceptRedirect();
            expect(BrowserData.getPaymentAcceptRedirect()).toBe(BrowserData.DEFAULTS.paymentAcceptRedirect);
        });
    });

});