(function (ng) {

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
                changePassword: nop,
                logout: nop,
                providerAuthenticate: nop,
                providerAuthorize: nop,
                forgotPassword: nop,
                recoverPassword: nop
            },
            organizations: {
                query: nop,
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
            activitySubscriptions: {
                add: nop,
                query: nop
            },
            balance: {
                createPayment: nop,
                cancelPayment: nop,
                executePayment: nop
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

})(angular);