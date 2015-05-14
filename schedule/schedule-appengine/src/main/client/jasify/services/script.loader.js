(function (angular) {

    /**
     *
     * Forked from https://github.com/urish/angular-load & changed to work with script id
     *
     */

    "use strict";

    angular
        .module('jasifyComponents')
        .service('angularLoad', ['$document', '$q', '$timeout', function ($document, $q, $timeout) {

            function loader(createElement) {
                var promises = {};

                return function (id, url) {
                    if (typeof promises[url] === 'undefined') {
                        var deferred = $q.defer();
                        var element = createElement(id, url);

                        element.onload = element.onreadystatechange = function (e) {
                            $timeout(function () {
                                deferred.resolve(e);
                            });
                        };
                        element.onerror = function (e) {
                            $timeout(function () {
                                deferred.reject(e);
                            });
                        };

                        promises[url] = deferred.promise;
                    }

                    return promises[url];
                };
            }

            /**
             * Dynamically loads the given script
             * @param src The url of the script to load dynamically
             * @returns {*} Promise that will be resolved once the script has been loaded.
             */
            this.loadScript = loader(function (id, src) {
                var script = $document[0].createElement('script');

                script.src = src;
                script.id = id;

                $document[0].body.appendChild(script);
                return script;
            });

            /**
             * Dynamically loads the given CSS file
             * @param href The url of the CSS to load dynamically
             * @returns {*} Promise that will be resolved once the CSS file has been loaded.
             */
            this.loadCSS = loader(function (href) {
                var style = $document[0].createElement('link');

                style.rel = 'stylesheet';
                style.type = 'text/css';
                style.href = href;

                $document[0].head.appendChild(style);
                return style;
            });
        }]);
})(angular);