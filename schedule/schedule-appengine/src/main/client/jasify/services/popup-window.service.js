(function (angular, jas) {
    /**
     * PopupWindow service - handles opening a new window and waiting for it to close across multiple devices
     */
    angular.module('jasifyComponents').factory('PopupWindow', popupWindowService);

    function popupWindowService($q, $interval, $window) {
        var PopupWindow = {
            mobile: jas.isMobile(),
            getOptions: getOptions,
            optionsString: optionsString,
            open: open,
            openWindow: undefined
        };

        function optionsString(options) {
            var parts = [];
            angular.forEach(options, function (value, key) {
                parts.push(key + '=' + value);
            });
            return parts.join(',');
        }

        function getOptions(options) {
            options = options || {};

            var defaults = {location: 0, status: 0, toolbar: 0, menubar: 0, resizable: 0, scrollbars: 1};
            defaults.width = options.width || 400;
            defaults.height = options.height || 550;

            if ($window.outerWidth) {
                defaults.left = Math.round(($window.outerWidth - defaults.width) / 2) + $window.screenX;
                defaults.top = Math.round(($window.outerHeight - defaults.height) / 2) + $window.screenY;
            } else if ($window.screen && $window.screen.width) {
                defaults.left = Math.round(($window.screen.width - defaults.width) / 2);
                defaults.top = Math.round(($window.screen.height - defaults.height) / 2);
            }

            return angular.extend(defaults, options);
        }

        function open(url, options) {
            options = options || {};

            if (PopupWindow.popupWindow) {
                return $q.reject('Another window is already open');
            }

            if (PopupWindow.mobile) {
                PopupWindow.popupWindow = $window.open(url, '_blank');
            } else {
                options = PopupWindow.getOptions(options);
                PopupWindow.popupWindow = $window.open(url, '_blank', PopupWindow.optionsString(options));
            }

            if (PopupWindow.popupWindow && PopupWindow.popupWindow.focus) {
                PopupWindow.popupWindow.focus();
            }

            var deferred = $q.defer();

            var stop = $interval(function () {
                if (PopupWindow.popupWindow && PopupWindow.popupWindow.closed) {
                    if (angular.isDefined(stop)) {
                        $interval.cancel(stop);
                        stop = undefined;
                    }
                    PopupWindow.popupWindow = undefined;
                    deferred.resolve('PopupWindow closed');
                }
            }, 200);
            return deferred.promise;
        }

        return PopupWindow;
    }
})(angular, jas);
