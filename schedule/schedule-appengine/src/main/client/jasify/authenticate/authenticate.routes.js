(function (angular) {
    angular.module('jasify.authenticate').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/oauth/:callbackUrl*', {
                templateUrl: 'authenticate/o-auth-2.html',
                controller: 'OAuth2Controller',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function ($q, $route, Allow, Activity) {
                        return Allow.all();
                    }
                }
            })
        ;
    }

})(angular);
