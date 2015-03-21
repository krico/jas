(function (angular) {
    "use strict";

    angular.module('jasifyComponents').factory('BrowserData', browserData);

    /**
     * This service centralizes all access to data stored on the browser.
     * The idea is that users don't need to know what key is used for what value, but rather have a more structured
     * way of accessing it.
     *
     *
     * @param $localStorage permanent storage on browser (even if closed)
     * @param $sessionStorage temporary storage on browser (deleted if browser is closed)
     */
    function browserData($localStorage, $sessionStorage) {
        var BrowserData = {
            DEFAULTS: {}
        };

        addProperty('paymentAcceptRedirect', '/balance/view', true);
        addProperty('firstAccess', true);
        addProperty('rememberUser');
        addProperty('loggedIn', false, true);

        /**
         * Creates the accessor/functions for handling a property.
         *
         *  - Sets a property BrowserData.DEFAULTS.${propertyName} = defaultValue
         *  - creates BrowserData.get${PropertyName} function that returns the set value or default
         *  - creates BrowserData.set${PropertyName} function that sets value
         *  - creates BrowserData.is${PropertyName}Set function returns true or false if the storage value is set
         *  - creates BrowserData.is${PropertyName}SessionOnly function returns true or false if the storage is session only or not
         *  - creates BrowserData.clear${PropertyName} function that resets the value
         *
         * @param propertyName
         * @param defaultValue
         * @param sessionOnly
         */
        function addProperty(propertyName, defaultValue, sessionOnly) {
            sessionOnly = !!sessionOnly;
            var storage = sessionOnly ? $sessionStorage : $localStorage;

            if (typeof defaultValue !== 'undefined') {
                BrowserData.DEFAULTS[propertyName] = defaultValue;
            } else {
                delete BrowserData.DEFAULTS[propertyName];
            }

            var beanName = angular.uppercase(propertyName.substring(0, 1)) + propertyName.substring(1);

            BrowserData['set' + beanName] = function (value) {
                storage[propertyName] = value;
            };

            BrowserData['get' + beanName] = function () {
                if (BrowserData['is' + beanName + 'Set']()) {
                    return storage[propertyName];
                }
                return BrowserData.DEFAULTS[propertyName];
            };

            BrowserData['is' + beanName + 'Set'] = function () {
                return typeof storage[propertyName] !== 'undefined';
            };

            BrowserData['is' + beanName + 'SessionOnly'] = function () {
                return sessionOnly;
            };

            BrowserData['clear' + beanName] = function () {
                delete storage[propertyName];
            };

        }

        return BrowserData;
    }
})(angular);