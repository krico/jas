(function (angular) {
    angular.module('jasify.checkoutWindow').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'checkout/checkout-window.html',
                controller: 'CheckoutWindowController',
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
            .when('/close', {
                templateUrl: 'checkout/checkout-window-close.html',
                controller: 'CheckoutWindowCloseController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.all();
                    }
                }
            })
        ;
    }

})(angular);
