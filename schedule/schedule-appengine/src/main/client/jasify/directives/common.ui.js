/**
 *
 * This module contains common UI elements (buttons, actions etc) and enforces common look & feel
 *
 */

(function (angular) {

    var module = angular.module('jasify.common.ui', []);

    module.directive('cardBack', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                description: '@'
            },
            template: '<a class="back-button"><button tooltip="Back" class="btn btn-default btn-icon"><i class="md mdi-undo"></i></button></a>',
            link: function (scope, element, attrs) {
                if (!attrs.ngHref && !attrs.href) {
                    throw  Error("ngHref is not defined on element");
                }
            }
        };
    });

    module.directive('addRow', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                description: '@'
            },
            template: '<button tooltip-append-to-body="true" tooltip="{{description}}" class="btn btn-primary btn-icon"><i class="md mdi mdi-add"></i></button>',
            link: function (scope, element, attrs) {
                if (!attrs.ngClick) {
                    throw Error("ngClick is not defined on element");
                }
                if (!attrs.description) {
                    throw  Error("description is not defined on element");
                }
            }
        };
    });

    module.directive('rowEdit', function(jasDialogs) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                description: '@',
                action: '&'
            },
            template: '<button tooltip="Edit" type="button" ng-click="action()" class="btn btn-icon btn-primary command-edit"><span class="md mdi mdi-edit"></span></button>',
            link: function (scope, element, attrs) {
                if (!attrs.action) {
                    throw Error("action is not defined on element");
                }
            }
        };
    });


    module.directive('rowDelete', function (jasDialogs) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                description: '@',
                action: '&'
            },
            template: '<button tooltip="Delete" ng-click="confirm()" tooltip="{{description}}" type="button" class="btn btn-icon btn-danger command-delete">' +
            '<span class="md mdi mdi-delete"></span></button>',
            link: function (scope, element, attrs) {
                if (!attrs.action) {
                    throw Error("action is not defined on element");
                }
                scope.confirm = function() {
                    jasDialogs.ruSure("", scope.action);
                };
            }
        };
    });


}(window.angular));

