(function (angular) {
    /**
     * Routes for all navbar links
     */
    angular.module('jasify').config(jasifyRoutes);

    //TODO: move routes to controller modules, eg. admin.routes.js test

    function jasifyRoutes($routeProvider) {
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
            .when('/signUp', {
                templateUrl: 'sign-up/sign-up.html',
                controller: 'SignUpController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/login', {
                templateUrl: 'login/login.html',
                controller: 'LoginController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.guest();
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
            /* BEGIN: Admin routes */
            .when('/admin/users', {
                templateUrl: 'admin/user/admin-users.html',
                controller: 'AdminUsersController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.admin();
                    }
                }
            })
            .when('/admin/user/:id?', {
                templateUrl: 'admin/user/admin-user.html',
                controller: 'AdminUserController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.admin();
                    }
                }
            })
            .when('/admin/groups', {
                templateUrl: 'admin/group/admin-groups.html',
                controller: 'AdminGroupsController',
                controllerAs: 'vm',
                resolve: {
                    groups: /*@ngInject*/ function ($q, Allow, Group) {
                        return Allow.admin().then(
                            function () {
                                return Group.query();
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            })
            .when('/admin/group/:id?', {
                templateUrl: 'admin/group/admin-group.html',
                controller: 'AdminGroupController',
                controllerAs: 'vm',
                resolve: {
                    group: /*@ngInject*/ function ($q, $route, Allow, Group) {
                        return Allow.admin().then(
                            function () {
                                if ($route.current.params.id)
                                    return Group.get($route.current.params.id);
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
            .when('/admin/organizations', {
                templateUrl: 'admin/organization/admin-organizations.html',
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
                templateUrl: 'admin/organization/admin-organization.html',
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
            .when('/admin/activities/:organizationId?', {
                templateUrl: 'admin/activity/admin-activities.html',
                controller: 'AdminActivitiesController',
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
                    },
                    activities: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.admin().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.organizationId) {
                                return Activity.query({organizationId: $route.current.params.organizationId});
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
            .when('/admin/activity/:id?', {
                templateUrl: 'admin/activity/admin-activity.html',
                controller: 'AdminActivityController',
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
                    },
                    activity: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.admin().then(allowed, forbidden);

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
        /* END: Admin routes */

    }

})(angular);