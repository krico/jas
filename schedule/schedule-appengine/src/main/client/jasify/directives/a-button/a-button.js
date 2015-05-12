/*global: window */

(function (angular) {

    "use strict";

    angular
        .module('jasifyComponents')
        .service('aButtonController', aButtonController)
        .directive('aButton', aButtonDirective);

    /**
     *
     * Animated buttons controller factory. It created predefined buttons that are used all over the app.
     *
     * @param $timeout
     * @returns {{create: Function, createSave: Function, createReset: Function, createPassword: Function, createProfileSave: Function, createProfileReset: Function}}
     */
    function aButtonController($timeout) {

        function ButtonController(options) {

            var self = this;

            self.options = options;
            self.inProgress = false;
            self.result = null;

            this.start = function (promise) {
                self.inProgress = true;
                if (promise) {
                    promise.then(self.success, self.error);
                }
            };

            this.pulse = function () {
                self.start();
                $timeout(self.success, 0);
            };

            this.end = function () {
                self.inProgress = false;
                self.result = null;
            };

            this.success = function () {
                self.result = 'success';
            };

            this.error = function () {
                self.result = 'error';
            };
        }

        return {
            create: function (options) {
                return new ButtonController(options);
            },
            createSave: function (options) {
                return new ButtonController(angular.extend({
                    buttonDefaultText: 'Save',
                    buttonSubmittingText: 'Saving...'
                }, options));
            },
            createReset: function (options) {
                return new ButtonController(angular.extend({
                    buttonDefaultText: 'Reset',
                    buttonSubmittingText: 'Reseting...',
                    buttonDefaultClass: 'btn-warning',
                    buttonSubmittingClass: 'btn-warning',
                    buttonSuccessText: 'Reset done'
                }, options));
            },
            createPassword: function () {
                return new ButtonController({
                    buttonDefaultClass: 'btn-warning',
                    buttonSubmittingClass: 'bgm-deeporange',
                    buttonDefaultText: 'Update Password',
                    buttonSubmittingText: 'Saving...',
                    buttonSuccessText: 'Password updated'
                });
            },
            createProfileSave: function () {
                return new ButtonController({
                    buttonDefaultText: 'Save',
                    buttonSubmittingText: 'Saving...',
                    buttonSuccessText: 'Profile updated'
                });
            },
            createProfileReset: function () {
                return new ButtonController({
                    buttonDefaultText: 'Reset',
                    buttonSubmittingText: 'Reseting...',
                    buttonDefaultClass: 'btn-warning',
                    buttonSubmittingClass: 'btn-warning',
                    buttonSuccessText: 'Profile restored'
                });
            }
        };
    }

    /**
     *
     * aButton - animated button.
     * Form button that gives feedback about initiated action (progress/done/error)
     *
     * @param $timeout
     * @param $mdMedia
     * @returns {{restrict: string, replace: boolean, scope: {controller: string, options: string}, controller: *[], template: string, link: Function}}
     */
    function aButtonDirective($timeout, $mdMedia) {

        return {
            restrict: 'AE',
            replace: true,
            scope: {
                controller: '=',
                options: '=?'
            },
            controller: ['$scope', function ($scope) {
                $scope.options = $scope.options || {};
                $scope.options = {
                    buttonDefaultClass: $scope.controller.options.buttonDefaultClass || $scope.options.buttonDefaultClass || 'btn-primary',
                    buttonSubmittingClass: $scope.controller.options.buttonSubmittingClass || $scope.options.buttonSubmittingClass || 'btn-primary',
                    buttonSuccessClass: $scope.controller.options.buttonSuccessClass || $scope.options.buttonSuccessClass || 'btn-primary',
                    buttonErrorClass: $scope.controller.options.buttonErrorClass || $scope.options.buttonErrorClass || 'btn-danger',
                    buttonSizeClass: $scope.controller.options.buttonSizeClass || $scope.options.buttonSizeClass || null,
                    buttonDefaultText: $scope.controller.options.buttonDefaultText || $scope.options.buttonDefaultText || 'Submit',
                    buttonSubmittingText: $scope.controller.options.buttonSubmittingText || $scope.options.buttonSubmittingText || 'Submitting...',
                    buttonSuccessText: $scope.controller.options.buttonSuccessText || $scope.options.buttonSuccessText || 'Completed',
                    buttonErrorText: $scope.controller.options.buttonErrorText || $scope.options.buttonErrorText || 'There was an error',
                    buttonInitialIcon: $scope.controller.options.buttonInitialIcon || $scope.options.buttonInitialIcon || 'glyphicon glyphicon-plus',
                    buttonSubmittingIcon: $scope.controller.options.buttonSubmittingIcon || $scope.options.buttonSubmittingIcon || 'glyphicon glyphicon-refresh',
                    buttonSuccessIcon: $scope.controller.options.buttonSuccessIcon || $scope.options.buttonSuccessIcon || 'glyphicon glyphicon-ok',
                    buttonErrorIcon: $scope.controller.options.buttonErrorIcon || $scope.options.buttonErrorIcon || 'glyphicon glyphicon-remove',
                    animationCompleteTime: $scope.controller.options.animationCompleteTime || $scope.options.animationCompleteTime || '2000',
                    iconsPosition: $scope.controller.options.iconsPosition || $scope.options.iconsPosition || 'left',
                    onlyIcons: $scope.controller.options.onlyIcons || $scope.options.onlyIcons || false
                };
            }],
            template: '<button class="btn {{buttonClass}} {{buttonSize}} {{onlyIcons}} btn-ng-bs-animated clearfix">' +
            '<div class="icons pull-{{iconsPosition}}">' +
            '<span class="{{buttonInitialIcon}} icon-initial"></span>' +
            '<span class="{{buttonSubmittingIcon}} icon-spinner icon-submit hidden"></span>' +
            '<span class="{{buttonSuccessIcon}} icon-result icon-success hidden"></span>' +
            '<span class="{{buttonErrorIcon}} icon-result icon-error hidden"></span>' +
            '</div>' +
            '<div class="text {{buttonTextFloatClass}}">{{buttonText}}</div>' +
            '</button>',
            link: function (scope, element) {
                var el = element;

                var icons = {
                    initial: angular.element(el[0].querySelector('.icon-initial')),
                    submitting: angular.element(el[0].querySelector('.icon-submit')),
                    result: angular.element(el[0].querySelectorAll('.icon-result')),
                    success: angular.element(el[0].querySelector('.icon-success')),
                    error: angular.element(el[0].querySelector('.icon-error'))
                };

                var endAnimation = function () {
                    scope.result = null;
                    scope.buttonClass = scope.options.buttonDefaultClass;
                    scope.buttonText = scope.options.buttonDefaultText;
                    el.removeClass('is-active').attr('disabled', false);
                    icons.result.addClass('hidden');
                };

                var setButtonTextFloatClass = function () {
                    if (scope.iconsPosition === 'left') {
                        return 'pull-right';
                    } else {
                        return 'pull-left';
                    }
                };

                scope.buttonClass = scope.options.buttonDefaultClass;
                scope.buttonSize = scope.options.buttonSizeClass;
                scope.formIsInvalid = scope.options.formIsInvalid;
                scope.iconsPosition = scope.options.iconsPosition;
                scope.buttonInitialIcon = scope.options.buttonInitialIcon;
                scope.buttonSubmittingIcon = scope.options.buttonSubmittingIcon;
                scope.buttonSuccessIcon = scope.options.buttonSuccessIcon;
                scope.buttonErrorIcon = scope.options.buttonErrorIcon;
                scope.iconsPosition = scope.options.iconsPosition;
                scope.buttonText = scope.options.buttonDefaultText;
                scope.buttonTextFloatClass = setButtonTextFloatClass();

                if (scope.options.onlyIcons) {
                    scope.onlyIcons = 'icons-only';
                }

                scope.$watch(function () {
                    return $mdMedia('sm');
                }, function (small) {
                    if (small) {
                        delete scope.buttonTextFloatClass;
                    } else {
                        scope.buttonTextFloatClass = setButtonTextFloatClass();
                    }
                });

                scope.$watch('controller.inProgress', function (newValue) {
                    if (newValue) {
                        scope.buttonClass = scope.options.buttonSubmittingClass;
                        scope.buttonText = scope.options.buttonSubmittingText;
                        el.prop('disabled', true).addClass('is-active');
                        icons.submitting.removeClass('hidden');
                    }
                }, true).bind(this);

                scope.$watch('controller.result', function (newValue) {
                    if (newValue === 'success') {
                        scope.buttonClass = scope.options.buttonSuccessClass;
                        scope.buttonText = scope.options.buttonSuccessText;
                        icons.submitting.addClass('hidden');
                        icons.success.removeClass('hidden');
                        $timeout(endAnimation, scope.options.animationCompleteTime);
                        scope.controller.end();
                    }
                    if (newValue === 'error') {
                        scope.buttonClass = scope.options.buttonErrorClass;
                        scope.buttonText = scope.options.buttonErrorText;
                        icons.submitting.addClass('hidden');
                        icons.error.removeClass('hidden');
                        $timeout(endAnimation, scope.options.animationCompleteTime);
                        scope.controller.end();
                    }
                }, true).bind(this);
            }
        };
    }

}(window.angular));