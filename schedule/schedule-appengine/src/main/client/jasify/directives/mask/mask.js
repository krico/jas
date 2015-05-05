(function(angular){
    "use strict";

    angular.module("jasifyComponents")
        .directive('mask', maskDirective)
        .factory('mask', maskService)
        .config(['$httpProvider', utilsConfiguration]);

    function maskDirective($rootScope, $window, mask) {

        return {
            restrict: 'A',
            scope: {},
            link: function ($scope) {

                mask.subscribe(function (request) {
                    toggle(request.show);
                });

                $scope.cancelMask = function () {
                    $window.location.reload();
                };

                $scope.show = false;

                function toggle(show) {
                    $scope.show = show;
                    $rootScope.$emit('mask-' + (show ? 'show' : 'hide'));
                }
            },
            template: '<div class="mask-background" layout="row" layout-align="center center" ng-class="{ \'in-progress\': show }"></div>' +
            '<div class="mask-content" layout="column" layout-align="center center" ng-class="{ \'in-progress\': show }">' +
            '<img src="http://jxnblk.com/loading/loading-spin.svg" alt="Loading icon" /> <h5>Please wait...</h5>' +
            '<div class="mask-cancel-message"><small>This operation is taking longer than expected. If you do not want to wait until operation is finished, ' +
            '   refresh the page and check the result of the operation. If the problem persists, please contact support.</small>' +
            '<button ng-click="cancelMask()" class="btn btn-warning btn-xs waves-effect">Refresh</button></div>' +
            '</div>'
        };
    }

    function maskService($q) {

        var observer,
            service = {
                hide: hide,
                show: show,
                subscribe: subscribe
            };

        return service;

        function subscribe(subscriber) {
            observer = subscriber;
        }

        function hide(promises) {
            if (angular.isArray(promises)) {
                $q.all(promises).then(function () {
                    maskToggle(false);
                }, function () {
                    maskToggle(false);
                });
            } else {
                maskToggle(false);
            }
        }

        function show() {
            maskToggle(true);
        }

        function maskToggle(showFlag) {
            if(observer) observer({
                show: showFlag
            });
        }
    }

    function utilsConfiguration($httpProvider) {

        var numLoadings = 0;

        function hideMask(mask, backgroundRequest) {
            if (!backgroundRequest) {
                if (!(--numLoadings)) {
                    mask.hide();
                }
            }
        }

        function showMask(mask, backgroundRequest) {
            if (!backgroundRequest) {
                numLoadings++;
                mask.show();
            }
        }

        $httpProvider.interceptors.push(['mask', '$q', function (mask, $q) {
            return {
                request: function (config) {
                    var backgroundRequest = config.background === undefined ? false : config.background;
                    showMask(mask, backgroundRequest);
                    return config || $q.when(config);
                },
                response: function (response) {
                    var backgroundRequest = response.config.background === undefined ? false : response.config.background;
                    hideMask(mask, backgroundRequest);
                    return response || $q.when(response);
                },
                responseError: function (rejection) {
                    var backgroundRequest = rejection.config.background === undefined ? false : rejection.config.background;
                    hideMask(mask, backgroundRequest);
                    return $q.reject(rejection);
                }
            };
        }]);
    }

}(angular));
