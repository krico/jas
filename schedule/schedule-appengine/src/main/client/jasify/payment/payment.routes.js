(function (angular) {
    angular.module('jasify.payment').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/payment/make/:amount?', {
                templateUrl: 'payment/payment-make.html',
                controller: 'PaymentMakeController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/payment/accept/:paymentId', {
                templateUrl: 'payment/payment-accept.html',
                controller: 'PaymentAcceptController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/payment/cancel/:paymentId', {
                templateUrl: 'payment/payment-cancel.html',
                controller: 'PaymentCancelController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, Allow) {
                        return Allow.user();
                    }
                }
            })
        ;
    }

})(angular);
