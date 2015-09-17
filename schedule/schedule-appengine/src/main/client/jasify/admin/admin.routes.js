(function (angular) {
    angular.module('jasify.admin').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
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
            .when('/admin/user/:id?/:created?', {
                templateUrl: 'admin/user/admin-user.html',
                controller: 'AdminUserController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.admin();
                    },
                    user: function (User, $route) {
                        return $route.current.params.id ?
                            User.get($route.current.params.id) : {};
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
                        return Allow.adminOrOrgMember().then(
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
                        return Allow.adminOrOrgMember().then(
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
                    organizations: /*@ngInject*/ function ($location, $route, $q, Allow, Organization) {

                        var dfd = $q.defer();

                        Allow.adminOrOrgMember().then(
                            function () {
                                Organization.query().then(function (result) {
                                    if ($route.current.params.organizationId) {
                                        dfd.resolve(result);
                                    } else {
                                        if (result.items && result.items.length > 0) {
                                            $location.path('/admin/activities/' + result.items[0].id);
                                        } else {
                                            dfd.resolve({items: []});
                                        }
                                    }
                                });
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );

                        return dfd.promise;
                    }
                }
            })
            .when('/admin/activity/:id?', {
                templateUrl: 'admin/activity/admin-activity.html',
                controller: 'AdminActivityController',
                controllerAs: 'vm',
                resolve: {
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
                    activity: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

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
            .when('/admin/activities/:activityId/subscribers', {
                templateUrl: 'admin/activity/admin-subscribers.html',
                controller: 'AdminSubscribersController',
                controllerAs: 'vm',
                resolve: {
                    subscriptions: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.activityId) {
                                return Activity.getSubscribers($route.current.params.activityId);
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
            .when('/admin/activities/:activityId/subscribe', {
                templateUrl: 'admin/activity/admin-subscribe.html',
                controller: 'AdminSubscribeController',
                controllerAs: 'vm',
                resolve: {
                    activity: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.activityId) {
                                return Activity.get($route.current.params.activityId);
                            } else {
                                return {};
                            }
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    },
                    subscriptions: /*@ngInject*/ function ($q, $route, Allow, Activity) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.activityId) {
                                return Activity.getSubscribers($route.current.params.activityId);
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
            .when('/admin/activity-types/:organizationId?', {
                templateUrl: 'admin/activity-type/admin-activity-types.html',
                controller: 'AdminActivityTypesController',
                controllerAs: 'vm',
                resolve: {
                    organizations: /*@ngInject*/ function ($location, $route, $q, Allow, Organization) {

                        var dfd = $q.defer();

                        Allow.adminOrOrgMember().then(
                            function () {
                                Organization.query().then(function (result) {
                                    if ($route.current.params.organizationId) {
                                        dfd.resolve(result);
                                    } else {
                                        if (result.items && result.items.length > 0) {
                                            $location.path('/admin/activity-types/' + result.items[0].id);
                                        } else {
                                            dfd.resolve({items: []});
                                        }
                                    }
                                });
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );

                        return dfd.promise;
                    },
                    activityTypes: /*@ngInject*/ function ($q, $route, Allow, ActivityType) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.organizationId) {

                                var dfd = $q.defer();

                                ActivityType.query($route.current.params.organizationId).then(function (result) {
                                    dfd.resolve(angular.extend({items: []}, result));
                                });

                                return dfd.promise;
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
            .when('/admin/activity-type/:id?', {
                templateUrl: 'admin/activity-type/admin-activity-type.html',
                controller: 'AdminActivityTypeController',
                controllerAs: 'vm',
                resolve: {
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
                    activityType: /*@ngInject*/ function ($q, $route, Allow, ActivityType) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.id) {
                                return ActivityType.get($route.current.params.id);
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
            .when('/admin/balances/:accountId?', {
                templateUrl: 'admin/balance/admin-balances.html',
                controller: 'AdminBalancesController',
                controllerAs: 'vm',
                resolve: {
                    accounts: /*@ngInject*/ function ($q, Allow, Balance) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            return Balance.getAccounts();
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    }
                }
            })
            .when('/admin/bookings', {
                templateUrl: 'admin/bookings/admin-bookings.html',
                controller: 'AdminBookingsController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.adminOrOrgMember();
                    }
                }
            })
            .when('/admin/activity-packages/:organizationId?', {
                templateUrl: 'admin/activity-package/admin-activity-packages.html',
                controller: 'AdminActivityPackagesController',
                controllerAs: 'vm',
                resolve: {
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
                    activityPackages: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

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
            .when('/admin/activity-package/:id?', {
                templateUrl: 'admin/activity-package/admin-activity-package.html',
                controller: 'AdminActivityPackageController',
                controllerAs: 'vm',
                resolve: {
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
                    activityPackage: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.id) {
                                return ActivityPackage.get($route.current.params.id);
                            } else {
                                return {};
                            }
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    },
                    activityPackageActivities: /*@ngInject*/ function ($q, $route, Allow, ActivityPackage) {

                        return Allow.adminOrOrgMember().then(allowed, forbidden);

                        function allowed() {
                            if ($route.current.params.id) {
                                return ActivityPackage.getActivities($route.current.params.id);
                            } else {
                                return [];
                            }
                        }

                        function forbidden(reason) {
                            return $q.reject(reason);
                        }
                    }
                }
            })
            .when('/admin/histories', {
                templateUrl: 'admin/history/admin-histories.html',
                controller: 'AdminHistoriesController',
                controllerAs: 'vm',
                resolve: {
                    histories: /*@ngInject*/ function ($q, Allow, History) {
                        return Allow.admin().then(
                            function () {
                                return History.query();
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            })
            .when('/admin/payments', {
                templateUrl: 'admin/payment/admin-payments.html',
                controller: 'AdminPaymentsController',
                controllerAs: 'vm',
                resolve: {
                    payments: /*@ngInject*/ function ($q, Allow, Payment) {
                        return Allow.admin().then(
                            function () {
                                return Payment.query();
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            })
            .when('/admin/payment/:id', {
                templateUrl: 'admin/payment/admin-payment.html',
                controller: 'AdminPaymentController',
                controllerAs: 'vm',
                resolve: {
                    payment: /*@ngInject*/ function ($q, $route, Allow, Payment) {
                        return Allow.admin().then(
                            function () {
                                if ($route.current.params.id) {
                                    return Payment.get($route.current.params.id);
                                } else {
                                    return {};
                                }
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
                    }
                }
            })
        ;
    }
})(angular);
