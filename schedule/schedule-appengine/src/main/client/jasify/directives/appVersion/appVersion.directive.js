/*global window */
(function(angular) {
    'use strict';

    angular.module('jasifyWeb').directive('appVersion', function() {

        return {
            restrict: 'E',
            replace: true,
            scope: {},
            templateUrl: 'directives/appVersion/appVersion.directive.html',
            controllerAs: 'vm',
            controller: function($log, VERSION, ApiSettings) {

                var self = this;

                self.VERSION = VERSION;
                self.SERVER_VERSION = { version: 'fetching' };

                getServerVersion();

                function getServerVersion() {
                    ApiSettings.getVersion().then(function (resp) {
                        angular.extend(self.SERVER_VERSION, resp);
                    }, function fail() {
                        $log.debug("Failed to get server version");
                    });
                }
            }
        };
    });

}(window.angular));