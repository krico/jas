(function (angular) {
    angular.module('jasify.admin').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        /* TODO:
        $routeProvider
            .when('/admin/users', {
                templateUrl: 'views/admin/users.html',
                controller: 'AdminUsersCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.admin();
                    }
                }
            })
            .when('/admin/user/:id?', {
                templateUrl: 'views/admin/user.html',
                controller: 'AdminUserCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.admin();
                    }
                }
            });
            */
    }

})(angular);
