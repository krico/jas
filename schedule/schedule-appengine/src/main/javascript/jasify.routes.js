(function () {
    /**
     * Routes for all navbar links
     */
    angular.module('jasify').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider.
            when('/', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/signUp', {
                templateUrl: 'views/signUp.html',
                controller: 'SignUpCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.guest();
                    }
                }
            })
            .when('/logout', {
                templateUrl: 'views/logout.html',
                controller: 'LogoutCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            })
            .when('/profile/:extra?', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.user();
                    }
                }
            })
            .when('/profile-logins', {
                templateUrl: 'views/profile-logins.html',
                controller: 'ProfileLoginsCtrl',
                resolve: {
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
            })
            /* BEGIN: Admin routes */
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
        /* END: Admin routes */
        //
        //otherwise({
        //    redirectTo: '/home'
        //});
    }

})();