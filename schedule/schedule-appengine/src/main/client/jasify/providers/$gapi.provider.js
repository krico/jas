(function (angular, gapi) {
    /**
     * To follow the "Angular way", instead of accessing gapi.client directly,
     * we provide the $gapi service and use it instead.  This allows us to
     * easily mock it for tests for example.
     */
    angular.module('jasifyComponents').provider('$gapi', $gapiProvider);

    function $gapiProvider() {
        this.$get = function ($window) {
            return gapi;
        };
    }

})(angular, gapi);