(function (angular) {
    angular.module('jasify.bookIt').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/:id?', {
                templateUrl: 'book-it/subscribe/book-it-subscribe.html',
                controller: 'BookItSubscribeController',
                controllerAs: 'vm',
                resolve: {
                    activity: /*@ngInject*/ function ($q, $route, Allow, Activity) {
                        return Allow.user().then(allowed, forbidden);


                        function allowed() {
                            if ($route.current.params.id) {
                                return Activity.get($route.current.params.id);
                            } else {
                                return {};
                            }
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
