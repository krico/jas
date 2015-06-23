(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowAnonymousController', CheckoutWindowAnonymousController);

    function CheckoutWindowAnonymousController($log, $location, ShoppingCart, Auth, $scope, AUTH_EVENTS, cart) {
        var vm = this;
        vm.auth = Auth;

        $scope.$on(AUTH_EVENTS.loginSuccess, goToCheckout);
        $scope.$on(AUTH_EVENTS.accountCreated, function () {
            $log.debug('Calling restore');
            Auth.restore(true).then(goToCheckout);
        });

        $log.debug('CheckoutWindowAnonymousController created');

        Auth.restore().then(goToCheckout);

        function goToCheckout() {
            $log.debug('Going to checkout, CART: ' + cart.id);
            return ShoppingCart.anonymousCartToUserCart(cart).then(function () {
                $location.path('/checkout');
            });//TODO: continue from here
        }
    }
})(angular);