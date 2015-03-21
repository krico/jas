(function (angular) {

    /**
     * ApiSettings - provides methods to get details on the server side api
     */
    angular.module('jasifyComponents').factory('ApiSettings', apiSettings);

    function apiSettings(Endpoint, $q) {
        var ApiSettings = {
            getVersion: getVersion,
            loaded: false,
            failed: false,
            versionData: {}
        };

        function getVersion() {
            if (ApiSettings.loaded) {
                if (ApiSettings.failed) {

                    return $q.reject();
                } else {

                    return $q.when(ApiSettings.versionData);
                }
            }

            return Endpoint.jasify(function (jasify) {
                return jasify.apiInfo().then(ok, fail);

                function ok(resp) {
                    ApiSettings.loaded = true;
                    ApiSettings.failed = false;
                    ApiSettings.versionData = resp.result;
                    return ApiSettings.versionData;
                }

                function fail(resp) {
                    ApiSettings.loaded = true;
                    ApiSettings.failed = true;
                    return $q.reject();
                }
            });

        }

        return ApiSettings;
    }

})(angular);