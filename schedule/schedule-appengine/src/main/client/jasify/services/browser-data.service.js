(function (angular) {
    "use strict";

    angular.module('jasifyComponents').factory('BrowserData', browserData);

    function browserData($rootScope, $log, localStorageService) {
        var BrowserData = {
            DEFAULTS: {}
        };

        $rootScope.$on('LocalStorageModule.notification.warning', function (m) {
            $log.warn('LocalStorageModule warning: ' + m);
        });

        $rootScope.$on('LocalStorageModule.notification.error', function (m) {
            $log.warn('LocalStorageModule error: ' + m);
        });

        addProperty('paymentAcceptRedirect', '/balance/view');
        addProperty('paymentCancelRedirect', '/payment/make');
        addProperty('paymentCancelRedirectAuto', false);
        addProperty('firstAccess', true);
        addProperty('rememberUser');
        addProperty('loggedIn', false);

        /**
         * Creates the accessor/functions for handling a property.
         *
         *  - Sets a property BrowserData.DEFAULTS.${propertyName} = defaultValue
         *  - creates BrowserData.get${PropertyName} function that returns the set value or default
         *  - creates BrowserData.set${PropertyName} function that sets value
         *  - creates BrowserData.is${PropertyName}Set function returns true or false if the storage value is set
         *  - creates BrowserData.clear${PropertyName} function that resets the value
         *
         * @param propertyName
         * @param defaultValue
         */
        function addProperty(propertyName, defaultValue) {

            if (typeof defaultValue !== 'undefined') {
                BrowserData.DEFAULTS[propertyName] = defaultValue;
            } else {
                delete BrowserData.DEFAULTS[propertyName];
            }

            var beanName = angular.uppercase(propertyName.substring(0, 1)) + propertyName.substring(1);

            BrowserData['set' + beanName] = function (value) {
                $log.debug('set' + beanName + ' = ' + value);
                localStorageService.set(propertyName, value);
            };

            BrowserData['get' + beanName] = function () {
                $log.debug('get' + beanName);
                if (BrowserData['is' + beanName + 'Set']()) {
                    $log.debug('get' + beanName + ' = ' + localStorageService.get(propertyName));
                    return localStorageService.get(propertyName);
                }
                $log.debug('(Default) get' + beanName + ' = ' + BrowserData.DEFAULTS[propertyName]);
                return BrowserData.DEFAULTS[propertyName];
            };

            BrowserData['is' + beanName + 'Set'] = function () {
                return localStorageService.get(propertyName) !== null;
            };

            BrowserData['clear' + beanName] = function () {
                $log.debug('clear' + beanName);
                localStorageService.remove(propertyName);
            };

        }

        return BrowserData;
    }
})(angular);