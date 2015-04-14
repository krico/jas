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
                controller: 'HomeController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/home', {
                templateUrl: 'home/home.html',
                controller: 'HomeController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
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
                controller: 'ProfileController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/profile-logins', {
                templateUrl: 'profile/profile-logins.html',
                controller: 'ProfileLoginsController',
                controllerAs: 'vm',
                resolve: {
                    logins: /*@ngInject*/ function ($q, Allow, UserLogin, Session) {
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
            })
        ;
        /* END: Admin routes */
    }

})(angular);