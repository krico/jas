(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowPageController', CheckoutWindowPageController);

    function CheckoutWindowPageController($log) {
        var vm = this;

        $log.debug('CheckoutWindowPageController created');


        /*
         BrowserData.setPaymentAcceptRedirect('/close');
         BrowserData.setPaymentCancelRedirect('/close');
         BrowserData.setPaymentCancelRedirectAuto(true);
         */
    }
})(angular);