(function (angular) {
    angular.module('jasify.balance').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/balance/view', {
                templateUrl: 'balance/balance-view.html',
                controller: 'BalanceViewController',
                controllerAs: 'vm',
                resolve: {
                    account: /*@ngInject*/ function ($q, Allow, Balance) {
                        return Allow.user().then(ok, fail);

                        function ok() {
                            return Balance.getAccount();
                        }

                        function fail(reason) {
                            return $q.reject(reason);
                        }
                    }
                }
            })
        ;
    }

})(angular);
