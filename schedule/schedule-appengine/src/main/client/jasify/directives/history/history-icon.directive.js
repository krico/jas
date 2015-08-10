/*global window */
(function (angular) {
    'use strict';

    var module = angular.module('jasifyComponents').directive('historyIcon', historyIconDirective);

    function historyIconDirective() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                history: '=history'
            },
            templateUrl: 'directives/history/history-icon.directive.html',
            link: function (scope, element, attrs) {
                scope.$watch('history', function (newValue) {
                    scope.history = newValue;
                    var ic = ['md', 'mdi', 'mdi-comment'];

                    if (newValue && newValue.type) {
                        if (newValue.type == 'Login') {
                            ic = ['glyphicon', 'glyphicon-log-in'];
                        }

                    }

                    scope.iconClass = ic;

                });
            }
        };
    }
}(window.angular));