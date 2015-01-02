(function (ng) {

    ng.module('jasify.mocks', ['ng'])
        .config(function ($provide) {

            $provide.decorator('$gapi', function ($gapiMock) {
                return $gapiMock;
            });

            $provide.decorator('$window', function ($windowMock) {
                return $windowMock;
            });
        });

    ng.module('jasify.mocks').factory('$gapiMock', $gapiMock);

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


        var $gapiMock = {data: {}};
        $gapiMock.client = {};
        $gapiMock.client.load = function (api, version, callback, path) {
            $gapiMock.data.load = {
                api: api,
                version: version,
                callback: callback,
                path: path,
                then: {}
            };
            return {
                then: function (success, fail) {
                    $gapiMock.data.load.then.success = success;
                    $gapiMock.data.load.then.fail = fail;
                }
            };
        };
        $gapiMock.client.jasify = jasify;
        return $gapiMock;
    }

    ng.module('jasify.mocks').factory('$windowMock', $windowMock);

    function $windowMock() {
        windowMock = {
            innerHeight: 400,
            innerWidth: 500
        };
        return windowMock;
    }

})(angular);