/**
 *
 * This module contains common UI elements (buttons, actions etc) and enforces common look & feel
 *
 */

(function (angular) {

    'use strict';

    var module = angular.module('jasify.common.ui', []);

    module.directive('paginationInfo', function () {

        /**
         * Helper class for computing pagination info
         * @param totalSize
         * @param currentPageSize
         * @param itemsPerPage
         * @param pageNumber
         * @constructor
         */
        function PaginationInfo(totalSize, currentPageSize, itemsPerPage, pageNumber) {

            var self = this;

            self.start = function () {
                if (currentPageSize === 0) {
                    return 0;
                }
                return (itemsPerPage * (pageNumber - 1)) + 1;
            };

            self.end = function () {
                if (currentPageSize === 0) {
                    return 0;
                }
                return Math.min(self.all(), self.start() + currentPageSize - 1);
            };

            self.all = function () {
                return totalSize;
            };
        }

        return {
            restrict: 'E',
            replace: true,
            template: '<div class="infos">Showing {{paginationInfo.start()}} to {{paginationInfo.end()}} of {{paginationInfo.all()}}</div>',
            scope: {
                'totalSize': '=',
                'currentPageSize': '=',
                'itemsPerPage': '=',
                'page': '='
            },
            controller: function ($scope) {

                function refresh() {
                    $scope.paginationInfo = new PaginationInfo($scope.totalSize, $scope.currentPageSize, $scope.itemsPerPage, $scope.page);
                }

                $scope.$watch('page', refresh);
                $scope.$watch('itemsPerPage', refresh);
                $scope.$watch('currentPageSize', refresh);
                $scope.$watch('totalSize', refresh);
            }
        };
    });

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
                    throw new Error("ngHref is not defined on element");
                }
            }
        };
    });

    module.directive('rowAdd', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                action: '&',
                description: '@'
            },
            template: '<button tooltip-append-to-body="true" tooltip="{{description}}" class="btn btn-primary btn-icon"><i class="md mdi mdi-add"></i></button>',
            link: function (scope, element, attrs) {
                //if (!attrs.action && !attrs.btnHref) {
                //    throw new Error("action or btnHref is required on element");
                //}
                if (!attrs.description) {
                    throw new Error("description is not defined on element");
                }
            }
        };
    });

    module.directive('rowEdit', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                description: '@',
                action: '&'
            },
            template: '<button tooltip="{{tooltip}}" type="button" ng-click="action()" class="btn btn-icon btn-primary command-edit"><span class="md mdi mdi-edit"></span></button>',
            link: function (scope, element, attrs) {

                scope.tooltip = 'Edit';

                if (!attrs.action) {
                    throw new Error("action is not defined on element");
                }

                attrs.$observe('description', function (newValue) {
                    scope.tooltip = newValue || 'Edit';
                });
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
                    throw new Error("action is not defined on element");
                }
                scope.confirm = function () {
                    jasDialogs.ruSure("", scope.action);
                };
            }
        };
    });

    module.directive('helpLabel', function () {
        return {
            replace: true,
            transclude: true,
            scope: {
                help: '@'
            },
            template: '<span tooltip-append-to-body="true" tooltip="{{help}}" class="help-label"><ng-transclude></ng-transclude></span>'
        };
    });

}(window.angular));

