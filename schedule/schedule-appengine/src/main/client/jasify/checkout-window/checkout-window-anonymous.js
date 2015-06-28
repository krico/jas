(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowAnonymousController', CheckoutWindowAnonymousController);

    function CheckoutWindowAnonymousController($log, $location, $scope, $cookies,
                                               ShoppingCart, Auth, BrowserData, AUTH_EVENTS,
                                               CHECKOUT_WINDOW, cart) {
        var vm = this;
        vm.auth = Auth;

        $scope.$on(AUTH_EVENTS.loginSuccess, goToCheckout);
        $scope.$on(AUTH_EVENTS.accountCreated, function () {
            $log.debug('Calling restore');
            Auth.restore(true).then(goToCheckout);
        });

        $log.debug('CheckoutWindowAnonymousController created');

        Auth.restore().then(goToCheckout,
            function () {
                $cookies[CHECKOUT_WINDOW.statusCookie] = CHECKOUT_WINDOW.statusAuthenticating;
            });

        function goToCheckout() {

            $cookies[CHECKOUT_WINDOW.statusCookie] = CHECKOUT_WINDOW.statusCheckout;

            BrowserData.setPaymentCancelRedirect('/close');
            BrowserData.setPaymentAcceptRedirect('/close');

            return ShoppingCart.anonymousCartToUserCart(cart).then(function () {
                $location.path('/checkout');
            });//TODO: continue from here
        }
    }
})(angular);