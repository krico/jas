/*global window */
(function (angular) {

    'use strict';

    /**
     * Constant for variables used to communicate between widgets and checkout window
     */
    angular.module('jasifyComponents').constant('CHECKOUT_WINDOW', {
        /* name of the cookie that holds checkout window status */
        statusCookie: 'checkoutWindowStatus',
        statusPaymentFailed: 'payment failed',
        statusSuccess: 'checkout succeeded',
        statusAuthenticating: 'authenticating',
        statusCheckout: 'checkout',
        foo: 'bar'
    });

})(window.angular);