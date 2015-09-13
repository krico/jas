/*global window */
(function (angular) {
    'use strict';

    var module = angular.module('jasifyComponents').directive('paymentState', paymentStateDirective);

    function paymentStateDirective() {
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                paymentState: '='
            },
            template: '<span ng-class="stateClass">{{paymentState}}</span>',
            link: function (scope, element, attrs) {
                scope.$watch('paymentState', function (newValue) {
                    scope.stateClass = 'payment-state-' + angular.lowercase(newValue);
                });
            }
        };
    }
}(window.angular));