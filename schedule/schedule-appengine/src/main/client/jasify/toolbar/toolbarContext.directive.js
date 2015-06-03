/*global window */
(function (angular) {

    'use strict';

    /**
     * Directive that drives context actions for toolbar
     */
    angular.module('jasify.common.ui').directive('toolbarContext', function ($mdMedia) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                scope.toolbar = element.closest('md-toolbar');
            },
            controller: function ($scope, toolbarContext) {
                $scope.hasContext = false;

                $scope.$watch(function () {
                    return toolbarContext.contextEnabled();
                }, function (newValue) {
                    $scope.showContext = newValue;
                    updateAccent();
                });

                function updateAccent() {
                    if ($scope.hasContext && $scope.showContext) {
                        $scope.toolbar.addClass('md-accent');
                    } else {
                        $scope.toolbar.removeClass('md-accent');
                    }
                }

                toolbarContext.subscribe(function (contextActions) {
                    $scope.hasContext = contextActions && contextActions.length > 0;
                    $scope.contextActions = contextActions;
                    updateAccent();
                });

                $scope.cancelContext = function () {
                    $scope.hasContext = false;
                    $scope.contextActions = [];
                    updateAccent();
                };
            }
        };
    });
}(window.angular));