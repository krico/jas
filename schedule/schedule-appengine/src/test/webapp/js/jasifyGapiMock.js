function jasifyGapiMock() {
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