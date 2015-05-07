(function (angular) {

    /**
     * Endpoint service, provide the glue between AngularJs and gapi (or $gapi).
     * It registers a 'global' function on $window that is called when the gapi client load
     * is finished.  You can call 'Endpoint.load()' to get a promise that get resolved when
     * the gapi client is loaded.  After that you can use either $gapi directly, or you can
     * use Endpoint to get the api.
     */
    angular.module('jasifyComponents').provider('Endpoint', EndpointProvider);

    function EndpointProvider() {

        var googleClientUrl = 'https://apis.google.com/js/client.js';
        var apiName = 'jasify';
        var apiVersion = 'v1';
        var apiPath = '/_ah/api';

        this.googleClientUrl = function (url) {
            if (url) googleClientUrl = url;
            return googleClientUrl;
        };

        this.apiName = function (name) {
            if (name) apiName = name;
            return apiName;
        };

        this.apiVersion = function (version) {
            if (version) apiVersion = version;
            return apiVersion;
        };

        this.apiPath = function (path) {
            if (path) apiPath = path;
            return apiPath;
        };

        this.$get = endpoint;

        function endpoint($log, $q, $window, $gapi) {

            var Endpoint = {
                init: init,
                load: load,
                googleClientUrl: googleClientUrl,
                errorHandler: errorHandler,
                jasify: jasify,
                jasifyLoaded: jasifyLoaded,
                isLoaded: isLoaded,
                loaded: false,
                promise: null,
                deferred: null,
                failed: false,
                settings: null
            };

            /**
             * Function to initialize google cloud endpoints
             */
            $window.endpointInitialize = function () {
                Endpoint.init();
            };

            function isLoaded() {
                return Endpoint.loaded;
            }


            function errorHandler(resp) {
                $log.debug("jasify() error: (" + resp.status + ") '" + resp.statusText + "'");

                return $q.reject(resp);
            }

            /**
             * Call a function with the jasify api
             */
            function jasify(fn) {
                return Endpoint.load().then(function () {
                    return $q.when(fn($gapi.client.jasify));
                }, Endpoint.errorHandler);
            }


            /**
             * Get a promise that gets resolved when the endpoint is loaded
             */
            function load() {
                if (Endpoint.loaded) {
                    var deferred = $q.defer();
                    if (Endpoint.failed) {
                        deferred.reject('Loading failed');
                    } else {
                        deferred.resolve('already loaded');
                    }
                    return deferred.promise;
                }
                if (Endpoint.promise !== null) {
                    return $q.when(Endpoint.promise); //loading
                }
                Endpoint.deferred = $q.defer();
                Endpoint.promise = Endpoint.deferred.promise;
                return Endpoint.promise;
            }

            function init() {
                $log.debug('Endpoint.init');
                if (Endpoint.promise === null) {
                    Endpoint.load(); //create promise
                }

                return $gapi.client.load(apiName, apiVersion, null, apiPath)
                    .then(Endpoint.jasifyLoaded, loadErrorHandler);

                function loadErrorHandler(r) {
                    $log.warn('Failed to load api: ' + r);
                    Endpoint.loaded = true;
                    Endpoint.failed = true;
                    Endpoint.promise = null;
                    Endpoint.deferred.reject('failed');
                    Endpoint.deferred = null;
                }
            }

            function jasifyLoaded() {
                Endpoint.loaded = true;
                Endpoint.promise = null;
                if (Endpoint.deferred) Endpoint.deferred.resolve('loaded');
                Endpoint.deferred = null;
                $log.debug('Endpoint.initialized');
            }

            return Endpoint;
        }
    }

})(angular);