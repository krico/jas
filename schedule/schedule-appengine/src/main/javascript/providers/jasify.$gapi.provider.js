(function () {
    /**
     * To follow the "Angular way", instead of accessing gapi.client directly,
     * we provide the $gapi service and use it instead.  This allows us to
     * easily mock it for tests for example.
     */
    angular.module('jasify').provider('$gapi', $gapiProvider);

    function $gapiProvider() {
        this.$get = function () {
            return gapi;
        };
    }

})();