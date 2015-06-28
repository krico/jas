(function (angular) {
    angular.module('jasify.checkoutWindow').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/anonymous-checkout/:cartId', {
                templateUrl: 'checkout-window/checkout-window-anonymous.html',
                controller: 'CheckoutWindowAnonymousController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.all();
                    },
                    cart: /*@ngInject*/ function ($route, ShoppingCart) {
                        return ShoppingCart.get($route.current.params.cartId);
                    }
                }
            })
            .when('/close', {
                templateUrl: 'checkout-window/checkout-window-close.html',
                controller: 'CheckoutWindowCloseController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.all();
                    }
                }
            })
            .otherwise({
                templateUrl: 'checkout-window/checkout-window-route-error.html',
                controller: 'CheckoutWindowRouteErrorController',
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
