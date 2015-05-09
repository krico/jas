(function (angular) {
    angular.module('jasify.activityPackage').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/activity-package', {
                templateUrl: 'activity-package/activity-package.html',
                controller: 'ActivityPackageController',
                controllerAs: 'vm',
                resolve: {
                    organizations: /*@ngInject*/ function ($q, Allow, Organization) {
                        return Allow.all().then(ok, nok);

                        function ok() {
                            return Organization.queryPublic();
                        }

                        function nok(r) {
                            return $q.reject(r);
                        }
                    }
                }
            })
        ;
    }

})(angular);
