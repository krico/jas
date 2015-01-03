(function (ng) {
    /**
     * Routes for all navbar links
     */
    ng.module('jasify').config(jasifyRoutes);

    //TODO: move routes to controller modules, eg. jasify.admin.routes.js test

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/home.html',
                controller: 'HomeController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/signUp', {
                templateUrl: 'views/signUp.html',
                controller: 'SignUpController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/logout', {
                templateUrl: 'views/logout.html',
                controller: 'LogoutController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/profile/:extra?', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileController',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/profile-logins', {
                templateUrl: 'views/profile-logins.html',
                controller: 'ProfileLoginsController',
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
            /* BEGIN: Admin routes */
            .when('/admin/users', {
                templateUrl: 'views/admin/users.html',
                controller: 'AdminUsersController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.admin();
                    }
                }
            })
            .when('/admin/user/:id?', {
                templateUrl: 'views/admin/user.html',
                controller: 'AdminUserController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.admin();
                    }
                }
            });
        /* END: Admin routes */

    }

})(angular);