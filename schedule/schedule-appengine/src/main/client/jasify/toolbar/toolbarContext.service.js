/*global window */
(function (angular) {

    'use strict';

    angular.module('jasify.common.ui').service('toolbarContext', function ($rootScope, $mdMedia, jasDialogs) {
        var listeners = [];

        /**
         *
         * Method to set context actions
         *
         * @param contextDescription Array of objects that describe context actions. Consists of type (eg. edit/bin) and action (handler to call when clicked)
         */
        function setContext(contextDescription) {
            var contextActions = [];

            if (!$mdMedia('sm')) {
                return;
            }

            angular.forEach(contextDescription, function (value) {

                var decorateHandler = null;

                if (value.type === 'bin') {
                    decorateHandler = function () {
                        jasDialogs.ruSure("", value.action);
                    };
                }

                contextActions.push(
                    {
                        type: value.type,
                        action: decorateHandler || value.action
                    }
                );
            });

            angular.forEach(listeners, function (value) {
                value(contextActions);
            });
        }

        /**
         *
         * Method to register listeners that will will be called on context change.
         * One candidate for listener is toolbar-context directive which is notified to update toolbar buttons
         *
         * @param listener
         */
        function subscribe(listener) {
            listeners.push(listener);
        }

        function clearContext() {
            setContext([]);
        }

        $rootScope.$on('$routeChangeStart', function () {
            clearContext();
        })

        return {
            clearContext: clearContext,
            setContext: setContext,
            subscribe: subscribe
        };
    });
}(window.angular));

