(function (angular) {
    angular.module('jasify.activityPackage').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/activity-packages/:organizationId?', {
                templateUrl: 'activity-package/activity-packages.html',
                controller: 'ActivityPackagesController',
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
                    },
                    activityPackages: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.all().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.organizationId) {
                                return ActivityPackage.query($route.current.params.organizationId);
                            } else {
                                return {items: []};
                            }
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    }
                }
            })
            .when('/activity-package/:activityPackageId', {
                templateUrl: 'activity-package/activity-package-subscribe.html',
                controller: 'ActivityPackageSubscribeController',
                controllerAs: 'vm',
                resolve: {
                    activityPackage: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.all().then(allowed, forbidden);

                        function allowed() {
                            return ActivityPackage.get($route.current.params.activityPackageId);
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    },
                    activityPackageActivities: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.all().then(allowed, forbidden);

                        function allowed() {
                            return ActivityPackage.getActivities($route.current.params.activityPackageId);
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    }

                }
            })
        ;
    }

})(angular);
