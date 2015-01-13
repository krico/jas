(function (angular) {
    /**
     * Routes for all navbar links
     */
    angular.module('jasify').config(jasifyRoutes);

    //TODO: move routes to controller modules, eg. jasify.admin.routes.js test

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/home.html',
                controller: 'HomeController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/signUp', {
                templateUrl: 'views/signUp.html',
                controller: 'SignUpController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/logout', {
                templateUrl: 'views/logout.html',
                controller: 'LogoutController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/profile/:extra?', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/profile-logins', {
                templateUrl: 'views/profile-logins.html',
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
            })
            .when('/admin/organizations', {
                templateUrl: 'views/admin/organizations.html',
                controller: 'AdminOrganizationsController',
                controllerAs: 'vm',
                resolve: {
                    organizations: /*@ngInject*/ function ($q, Allow, Organization) {
                        return Allow.admin().then(
                            function () {
                                return Organization.query();
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            })
            .when('/admin/organization/:id?', {
                templateUrl: 'views/admin/organization.html',
                controller: 'AdminOrganizationController',
                controllerAs: 'vm',
                resolve: {
                    organization: /*@ngInject*/ function ($q, $route, Allow, Organization) {
                        return Allow.admin().then(
                            function () {
                                if ($route.current.params.id)
                                    return Organization.get($route.current.params.id);
                                else
                                    return {};
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