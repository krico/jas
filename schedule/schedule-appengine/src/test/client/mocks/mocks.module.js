(function (ng, window) {
    window.GLOBAL_GOOGLE_CLIENT_LOADED = true;

    ng.module('jasify.mocks', ['ng'])
        .config(function ($provide) {

            $provide.decorator('$gapi', function ($gapiMock) {
                return $gapiMock;
            });

            $provide.decorator('$window', function ($windowMock) {
                return $windowMock;
            });

            $provide.decorator('$modal', function ($modalMock) {
                return $modalMock;
            });

            $provide.decorator('localStorageService', function () {
                var mock = {data: {}};
                mock.set = function (k, v) {
                    mock.data[k] = v;
                };
                mock.get = function (k) {
                    if (typeof mock.data[k] === 'undefined') {
                        return null;
                    }
                    return mock.data[k];
                };
                mock.remove = function (k, v) {
                    delete mock.data[k];
                };
                return mock;
            });
        });

    ng.module('jasify.mocks').factory('$gapiMock', $gapiMock);

    ng.module('jasify.mocks').factory('$windowMock', $windowMock);

    ng.module('jasify.mocks').factory('$modalMock', $modalMock);

    function $gapiMock() {
        var nop = angular.noop;
        /* we mock all api calls, you can always override or spyOn them */
        var jasify = {
            apiInfo: nop,
            userLogins: {
                list: nop,
                remove: nop
            },
            carts: {
                get: nop,
                createAnonymousCart: nop,
                anonymousCartToUserCart: nop,
                removeItem: nop,
                addUserActivity: nop,
                getUserCart: nop,
                clearUserCart: nop
            },
            users: {
                query: nop,
                get: nop,
                update: nop,
                add: nop
            },
            unique: {
                username: nop,
                email: nop
            },
            auth: {
                login: nop,
                restore: nop,
                changePassword: nop,
                logout: nop,
                providerAuthenticate: nop,
                providerAuthorize: nop,
                forgotPassword: nop,
                recoverPassword: nop
            },
            organizations: {
                query: nop,
                queryPublic: nop,
                get: nop,
                update: nop,
                add: nop,
                remove: nop,
                users: nop,
                addUser: nop,
                removeUser: nop,
                addGroup: nop,
                removeGroup: nop,
                groups: nop
            },
            groups: {
                query: nop,
                get: nop,
                update: nop,
                add: nop,
                addUser: nop,
                removeUser: nop,
                users: nop,
                remove: nop
            },
            activityTypes: {
                query: nop,
                get: nop,
                update: nop,
                add: nop,
                remove: nop
            },
            activities: {
                query: nop,
                get: nop,
                update: nop,
                add: nop,
                remove: nop
            },
            activityPackages: {
                query: nop,
                get: nop,
                update: nop,
                addActivity: nop,
                removeActivity: nop,
                add: nop,
                getActivities: nop,
                remove: nop
            },
            activitySubscriptions: {
                add: nop,
                query: nop,
                subscribers: nop,
                cancel: nop,
                getForUser: nop
            },
            balance: {
                createPayment: nop,
                cancelPayment: nop,
                getAccount: nop,
                getAccounts: nop,
                getTransactions: nop,
                createCheckoutPayment: nop,
                getPaymentInvoice: nop,
                executePayment: nop
            },
            payments: {
                getPaymentInvoice: nop,
                query: nop
            },
            histories: {
                query: nop
            }
        };


        var mock = {data: {}};
        mock.client = {};
        mock.client.load = function (api, version, callback, path) {
            mock.data.load = {
                api: api,
                version: version,
                callback: callback,
                path: path,
                then: {}
            };
            return {
                then: function (success, fail) {
                    mock.data.load.then.success = success;
                    mock.data.load.then.fail = fail;
                }
            };
        };
        mock.client.jasify = jasify;
        return mock;
    }

    function $windowMock() {
        var mock = {
            // These two remove the warnings from ngstorage
            localStorage: {}, sessionStorage: {},
            innerHeight: 400,
            innerWidth: 500
        };
        return mock;
    }

    function $modalMock() {
        var mock = {
            data: {open: {}},
            open: function () {
                return {
                    result: {
                        then: function (confirmCallback, cancelCallback) {
                            mock.data.open.confirmCallback = confirmCallback;
                            mock.data.open.cancelCallback = cancelCallback;
                        }
                    }
                };
            }
        };
        return mock;
    }

})(angular, window);