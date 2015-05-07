(function (angular) {

    /**
     * Endpoint service, provide the glue between AngularJs and gapi (or $gapi).
     * It registers a 'global' function on $window that is called when the gapi client load
     * is finished.  You can call 'Endpoint.load()' to get a promise that get resolved when
     * the gapi client is loaded.  After that you can use either $gapi directly, or you can
     * use Endpoint to get the api.
     * To configure values in the endpoint in your app config you can do:
     * function (EndpointProvider) {
     *       EndpointProvider.apiName('myApiName');
     *       EndpointProvider.apiVersion('myApiVersion');
     *       EndpointProvider.apiPath('https://my/api/path');
     *       EndpointProvider.googleClientUrl('https://my/google/client.js');
     *  })
     * Check the "describe('Config'" section of this provider's spec.
     */
    angular.module('jasifyComponents').provider('Endpoint', EndpointProvider);

    function EndpointProvider() {
        var beVerbose = false;
        var googleClientUrl = 'https://apis.google.com/js/client.js';
        var apiName = 'jasify';
        var apiVersion = 'v1';
        var apiPath = '/_ah/api';
        var googleClientLoaded = false;

        this.verbose = function (v) {
            if (v) beVerbose = v;
            return beVerbose;
        };
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

        function endpoint($log, $q, $timeout, $window, $document, $gapi) {

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
                loadGoogleClient: loadGoogleClient,
                googleClientLoaded: false,
                settings: null
            };

            Endpoint.loadGoogleClient(); // Load the google client script when service is instantiated

            function verbose(msg){
                if(beVerbose) $log.debug(msg);
            }

            function loadGoogleClient() {

                if (googleClientLoaded) return;
                verbose('Loading google client from [' + Endpoint.googleClientUrl + ']');

                googleClientLoaded = true;

                var script = $document[0].createElement('script');
                script.onload = function (e) {
                    $timeout(function () {
                        verbose('client.js loaded');
                    });
                };

                script.onerror = function (e) {
                    $timeout(function () {
                        $log.info('client.js loading failed: ' + angular.toJson(e));
                        Endpoint.init(); //make it fail
                    });
                };

                $window.endpointOnLoad = function () {
                    $timeout(function () {
                        Endpoint.init();
                    });
                };
                script.src = Endpoint.googleClientUrl + '?onload=endpointOnLoad';
                $document[0].body.appendChild(script);
            }

            function isLoaded() {
                return Endpoint.loaded;
            }


            function errorHandler(resp) {
                if (resp.status)
                    $log.debug('jasify() error: (' + resp.status + ') ' + resp.statusText);
                else
                    $log.debug('Jasify error: ' + angular.toJson(resp));
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
                verbose('Endpoint.init');
                if (Endpoint.promise === null) {
                    Endpoint.load(); //create promise
                }

                if ($gapi.client === undefined) {
                    loadErrorHandler('Failed to load google client from [' + Endpoint.googleClientUrl + ']');
                    return;
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
                verbose('Endpoint.initialized');
            }

            return Endpoint;
        }
    }

})(angular);