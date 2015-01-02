(function () {
    /**
     * Popup services (windows)
     * Inspired by satelizer (https://github.com/sahat/satellizer)
     */
    angular.module('jasify').factory('Popup', popup);

    function popup($log, $q, $interval, $window) {
        var popupWindow = null;
        var waiting = null;

        var Popup = {
            getOptions: getOptions,
            optionsString: optionsString,
            open: open,
            popupWindow: popupWindow
        };


        var Providers = {
            Google: {},
            Facebook: {height: 269}
        };

        function getOptions(options) {
            options = options || {};
            var width = options.width || 500;
            var height = options.height || 500;
            return angular.extend({
                width: width,
                height: height,
                left: $window.screenX + (($window.outerWidth - width) / 2),
                top: $window.screenY + (($window.outerHeight - height) / 2.5)
            }, options);
        }

        function optionsString(options) {
            var parts = [];
            angular.forEach(options, function (value, key) {
                parts.push(key + '=' + value);
            });
            return parts.join(',');
        }

        function open(url, provider) {
            var opts = {};
            if (provider && Providers[provider]) {
                opts = Providers[provider];
            }
            var optStr = Popup.optionsString(Popup.getOptions(opts));
            popupWindow = $window.open(url, '_blank', optStr);
            if (popupWindow && popupWindow.focus) {
                popupWindow.focus();
            }
            var deferred = $q.defer();

            waiting = $interval(function () {
                try {
                    if (popupWindow.document &&
                        popupWindow.document.readyState == 'complete' &&
                        popupWindow.document.domain === document.domain &&
                        popupWindow.location &&
                        popupWindow.location.pathname.indexOf('/oauth2/callback') === 0) {
                        var script = popupWindow.document.getElementById("json-response");
                        popupWindow.close();
                        $interval.cancel(waiting);
                        popupWindow = null;
                        if (script && script.text) {
                            var r = angular.fromJson(script.text);
                            deferred.resolve(r);
                        } else {
                            deferred.reject('Bad response...');
                        }
                    }
                } catch (error) {
                    $log.debug("E: " + error);
                }

                if (popupWindow && popupWindow.closed) {
                    $interval.cancel(waiting);
                    popupWindow = null;
                    deferred.reject('Authorization failed (window closed)');
                }
            }, 34);
            return deferred.promise;
        }

        return Popup;
    }

})();