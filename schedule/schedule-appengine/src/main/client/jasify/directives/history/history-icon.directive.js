/*global window */
(function (angular) {
    'use strict';

    var module = angular.module('jasifyComponents').directive('historyIcon', historyIconDirective);

    function historyIconDirective() {
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
            Logout: ['ion-android-exit']
        };
        var defaultTextClass = [];
        var textClassMap = {
            AccountCreated: ['text-success'],
            AccountCreationFailed: ['text-danger'],
            PasswordForgottenFailed: ['text-danger'],
            PasswordRecovered: ['text-success'],
            LoginFailed: ['text-danger']
        };
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
                    var type = newValue && newValue.type || '';

                    scope.iconClass = iconClassMap[type] || defaultIconClass;
                    scope.textClass = textClassMap[type] || defaultTextClass;
                });
            }
        };
    }
}(window.angular));