(function (angular) {
    angular.module('jasify.checkout').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/checkout', {
                templateUrl: 'checkout/checkout.html',
                controller: 'CheckoutController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.user();
                    },
                    cart: /*@ngInject*/ function ($q, ShoppingCart) {
                        return ShoppingCart.getUserCart();
                    }
                }
            })
        ;
    }

})(angular);
