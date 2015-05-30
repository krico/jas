/*global window */
(function (angular) {

    'use strict';

    var jasifyComponents = angular.module('jasifyComponents');

    jasifyComponents.directive('jasProgress', function () {
        return {
            restrict: 'A',
            controller: function ($scope, $rootScope, $timeout, $interval) {

                var showAfter = 200,
                    progressUpdateAfter = 100,
                    progressPromise,
                    timeoutPromise,
                    startProgress,
                    endProgress;

                $rootScope.$on('$routeChangeStart', function () {
                    startProgress();
                });

                $rootScope.$on('$routeChangeSuccess', function () {
                    endProgress();
                });

                $rootScope.$on('$routeChangeError', function () {
                    endProgress();
                });

                startProgress = function () {

                    endProgress();

                    $scope.mode = 'waiting';
                    timeoutPromise = $timeout(function () {
                        if ($scope.mode === 'waiting') {
                            $scope.mode = 'buffer';
                            $scope.determinateValue = 10;
                            $scope.determinateValue2 = 30;
                            progressPromise = $interval(function () {
                                $scope.determinateValue += 1;
                                $scope.determinateValue2 += 2;
                                if ($scope.determinateValue > 100) {
                                    $scope.determinateValue = 10;
                                    $scope.determinateValue2 = 20;
                                }
                                if ($scope.determinateValue2 > 100) {
                                    $scope.determinateValue2 = $scope.determinateValue;
                                }
                            }, progressUpdateAfter, 0, true);
                        }
                    }, showAfter);
                };

                endProgress = function () {
                    $scope.mode = 'done';
                    $interval.cancel(progressPromise);
                    $timeout.cancel(timeoutPromise);
                };
            }
        };
    });

}(window.angular));