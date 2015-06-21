(function (angular) {
    /**
     * Routes for all navbar links
     */
    angular.module('jasifyWeb').config(jasifyWebRoutes);

    //TODO: move routes to controller modules, eg. admin.routes.js test

    function jasifyWebRoutes($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'home/home.html',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/home', {
                templateUrl: 'home/home.html',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/widgets', {
                templateUrl: 'widgets/widgets.html',
                controller: 'WidgetsController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    },
                    organizations: /*@ngInject*/ function ($q, Allow, Organization) {
                        return Allow.adminOrOrgMember().then(
                            function () {
                                return Organization.query();
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    },
                }
            })
            .when('/logout', {
                templateUrl: 'logout/logout.html',
                controller: 'LogoutController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/profile/:extra?', {
                templateUrl: 'profile/profile.html',
                controller: function($scope, logins) {
                    $scope.logins = logins;
                },
                resolve: {
                    allow: function (Allow) {
                        return Allow.user();
                    },
                    logins: function ($q, Allow, UserLogin, Session) {
                        return Allow.user().then(
                            function () {
                                return UserLogin.list(Session.userId);
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            });
    }

})(angular);