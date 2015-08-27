/*global window */
(function (angular) {
    'use strict';

    var module = angular.module('jasifyComponents').directive('historyIcon', historyIconDirective);

    function historyIconDirective($log) {
        var defaultIconClass = ['md', 'mdi', 'mdi-comment'];
        var iconClassMap = {
            Login: ['glyphicon', 'glyphicon-log-in'],
            LoginFailed: ['glyphicon', 'glyphicon-remove-circle'],
            PasswordChanged: ['ion-android-lock'],
            PasswordForgotten: ['ion-ios-locked'],
            PasswordForgottenFailed: ['ion-ios-locked'],
            PasswordRecovered: ['ion-ios-unlocked'],
            AccountCreated: ['mdi', 'mdi-person'],
            AccountCreationFailed: ['glyphicon', 'glyphicon-remove-circle'],
            Logout: ['ion-android-exit'],
            SubscriptionCreated: ['glyphicon', 'glyphicon glyphicon-ok'],
            SubscriptionCreationFailed: ['glyphicon', 'glyphicon-remove-circle'],
            SubscriptionCancelled: ['glyphicon', 'glyphicon glyphicon-remove'],
            SubscriptionCancellationFailed: ['glyphicon', 'glyphicon-remove-circle']
        };
        var defaultTextClass = [];
        var textClassMap = {
            AccountCreated: ['text-success'],
            AccountCreationFailed: ['text-danger'],
            PasswordForgottenFailed: ['text-danger'],
            PasswordRecovered: ['text-success'],
            LoginFailed: ['text-danger'],
            SubscriptionCreated: ['text-success'],
            SubscriptionCreationFailed: ['text-danger'],
            SubscriptionCancelled: ['text-success'],
            SubscriptionCancellationFailed: ['text-danger']
        };
        return {
            restrict: 'E',
            replace: true,
            scope: {
                history: '=',
                historyType: '@'
            },
            templateUrl: 'directives/history/history-icon.directive.html',
            link: function (scope, element, attrs) {
                scope.$watch('historyType', function (newValue) {
                    var type = newValue;
                    if (type) {
                        scope.historyType = newValue;
                    }
                    scope.iconClass = iconClassMap[type] || defaultIconClass;
                    scope.textClass = textClassMap[type] || defaultTextClass;
                });
                scope.$watch('history', function (newValue) {
                    scope.history = newValue;
                    if (newValue) {
                        scope.historyType = newValue && newValue.type || '';
                    }
                });
            }
        };
    }
}(window.angular));