/*global window */
(function (angular) {

    'use strict';

    /**
     * Directive that drives context actions for toolbar
     */
    angular.module('jasify.common.ui').directive('toolbarContext', function ($mdMedia) {
        return {
            restrict: 'A',
            controller: function ($scope, toolbarContext) {
                $scope.hasContext = false;

                $scope.$watch(function () {
                    return $mdMedia('sm');
                }, function (newValue) {
                    $scope.showContext = newValue;
                });

                toolbarContext.subscribe(function (contextActions) {
                    $scope.hasContext = contextActions && contextActions.length > 0;
                    $scope.contextActions = contextActions;
                });

                $scope.cancelContext = function () {
                    $scope.hasContext = false;
                    $scope.contextActions = [];
                };
            }
        };
    });
}(window.angular));