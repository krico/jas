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
        /* we mock all api calls, you can always override or spyOn them */
        var jasify = {
            apiInfo: angular.noop,
            userLogins: {
                list: angular.noop,
                remove: angular.noop
            },
            username: {
                check: angular.noop
            },
            auth: {
                login: angular.noop,
                changePassword: angular.noop,
                logout: angular.noop
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