/*global window */
(function (angular) {
    'use strict';

    angular.module('jasifyWeb').directive('appVersion', function () {

        return {
            restrict: 'E',
            replace: true,
            scope: {
                size: '@displaySize'
            },
            templateUrl: 'directives/appVersion/appVersion.directive.html',
            controllerAs: 'vm',
            controller: function ($log, $scope, VERSION, ApiSettings, jasDialogs, tsToDateFilter) {
                $log.debug('SIZE: ' + $scope.size);
                var vm = this;
                if ($scope.size == 'compact') {
                    vm.fullSize = false;
                    vm.compactSize = true;
                } else {
                    vm.fullSize = true;
                    vm.compactSize = false;
                }
                vm.VERSION = VERSION;
                vm.SERVER_VERSION = {version: 'fetching'};
                vm.showVersionDialog = showVersionDialog;


                getServerVersion();

                function getServerVersion() {
                    ApiSettings.getVersion().then(function (resp) {
                        angular.extend(vm.SERVER_VERSION, resp);
                    }, function fail() {
                        $log.debug("Failed to get server version");
                    });
                }

                function showVersionDialog() {
                    var frontend = '** Frontend **\n' +
                        '\nV=' + vm.VERSION.version +
                        '\nN=' + vm.VERSION.number +
                        '\nB=' + vm.VERSION.branch +
                        '\nT=' + tsToDateFilter(vm.VERSION.timestamp, 'L LT') +
                        '\n';

                    var backend = '** Backend ** \n' +
                        '\nV=' + vm.SERVER_VERSION.version +
                        '\nN=' + vm.SERVER_VERSION.number +
                        '\nB=' + vm.SERVER_VERSION.branch +
                        '\nT=' + tsToDateFilter(vm.SERVER_VERSION.timestamp, 'L LT') +
                        '\n';

                    jasDialogs.success(frontend + '\n' + backend);
                }
            }
        };
    });

}(window.angular));