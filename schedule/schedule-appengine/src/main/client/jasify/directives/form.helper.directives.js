/*global window,autosize */

/**
 * Form directives:
 * - jasFeedbackIconPending, jasFeedbackIconInvalid, jasFeedbackIconValid for feedback icons
 * - jasErrorHelpBlock, jasHasFeedback for general feedback (like error messages)
 * - fgInput, fgFloat for floating labels support
 */

(function (angular, $) {

    "use strict";

    var jasifyDirectivesFormModule = angular.module('jasifyComponents');

    jasifyDirectivesFormModule.run(function ($rootScope) {

        $('body').on('focus', '.form-control', function () {
            $(this).closest('.fg-line').addClass('fg-toggled');
        });


        $('body').on('blur', '.form-control', function () {
            var p = $(this).closest('.form-group');
            var i = p.find('.form-control').val();

            if (p.hasClass('fg-float')) {
                if (i.length === 0) {
                    $(this).closest('.fg-line').removeClass('fg-toggled');
                }
            } else {
                $(this).closest('.fg-line').removeClass('fg-toggled');
            }
        });

        $rootScope.$on("$routeChangeSuccess", function () {
            $('md-content').scrollTop(0);
        });
    });

    jasifyDirectivesFormModule.provider('$moment', function () {
        this.$get = ['$window', function ($window) {
            return $window.moment;
        }];
    });

    jasifyDirectivesFormModule.directive('btnHref', function ($location) {
        return function (scope, element, attrs) {
            var path;

            attrs.$observe('btnHref', function (val) {
                path = val;
            });

            element.bind('click', function () {
                scope.$apply(function () {
                    $location.path(path);
                });
            });
        };
    });

    jasifyDirectivesFormModule.directive('wizardTrigger', function () {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                elem.on('click', function () {
                    var tabName = attrs.wizardTrigger;
                    $('[href="' + tabName + '"]').click();
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('prevent', function () {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                elem.on(attrs.prevent, function (e) {
                    e.preventDefault();
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('formWizardBasic', function ($log) {
        return {
            restrict: 'C',
            link: function (scope, element, attrs) {
                var options = {tabClass: 'fw-nav'};
                if (attrs.formWizardBasic) {
                    var extraOptions = scope.$eval(attrs.formWizardBasic);
                    if (angular.isObject(extraOptions)) {
                        angular.extend(options, extraOptions);
                    } else {
                        $log.error('formWizardBasic must evaluate to an object [' + attrs.formWizardBasic +
                        '] evaluates to [' + angular.toJson(extraOptions) + ']');
                    }
                }

                $(element).bootstrapWizard(options);
            }
        };
    });

    jasifyDirectivesFormModule.directive('autoSize', function () {
        return {
            restrict: 'C',
            link: function (scope, element, attrs) {

                function initialize() {
                    if (autosize) {
                        autosize(element);
                    }
                }

                var unwatch = scope.$watch(attrs.ngModel, function (newValue, oldValue) {
                    initialize();
                    unwatch();
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('fgInput', function () {
        return {
            restrict: 'C',
            link: function (scope, element, attrs, controllers) {
                scope.$watch(attrs.ngModel, function (newValue) {
                    if (!!newValue) {
                        $(element).parent('.fg-line').addClass('fg-toggled');
                    }
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('fgFloat', function () {
        return {
            restrict: 'C',
            link: function (scope, element, attrs) {
                $('.form-control', element).each(function () {
                    var i = $(this).val();
                    if (i !== null && i.length !== 0) {
                        $(this).closest('.fg-line').addClass('fg-toggled');
                    }
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('jasErrorHelpBlock', function () {
        return {
            replace: true,
            restrict: 'E',
            transclude: true,
            scope: {
                field: '=',
                form: '='
            },
            template: '<small><span class="help-block has-error"' +
            'ng-if="(form.$submitted || field.$touched) && field.$invalid"' +
            'ng-messages=field.$error><ng-transclude></ng-transclude></span></small>'
        };
    });

    jasifyDirectivesFormModule.directive('jasHasFeedback', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {

                var $element = $(element),
                    formName = $element.closest('form').attr('name'),
                    formFieldName = $element.find('input, select, textarea').attr('name');

                if (!formName || !formFieldName) {
                    return;
                }

                scope.$watch(formName + '.' + formFieldName + '.$touched', function () {
                    updateClasses();
                });

                scope.$watch(formName + '.' + formFieldName + '.$valid', function () {
                    updateClasses();
                });

                scope.$watch(formName + '.$submitted', function () {
                    updateClasses();
                });

                function updateClasses() {

                    var form = scope.$eval(formName);
                    if (form[formFieldName].$touched && form[formFieldName].$valid) {
                        $element.addClass('has-success');
                    } else {
                        $element.removeClass('has-success');
                    }

                    if ((form.$submitted || form[formFieldName].$touched) && form[formFieldName].$invalid) {
                        $element.addClass('has-error');
                    } else {
                        $element.removeClass('has-error');
                    }

                    $element.toggleClass('has-feedback', element.hasClass('has-error') || element.hasClass('has-success'));
                }
            }
        };
    });

    jasifyDirectivesFormModule.directive('jasFeedbackIconValid', function () {
        return {
            multiElement: true,
            restrict: 'E',
            replace: true,
            scope: {
                field: '='
            },
            template: '<i ng-show="field.$valid && field.$touched" class="md mdi mdi-check form-control-feedback"></i>'
        };
    });

    jasifyDirectivesFormModule.directive('jasFeedbackIconInvalid', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                field: '=',
                form: '='
            },
            template: '<i ng-show="(form.$submitted || field.$touched) && field.$invalid" class="md mdi mdi-close form-control-feedback"></i>'
        };
    });

    jasifyDirectivesFormModule.directive('jasFeedbackIconPending', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                field: '='
            },
            template: '<span class="form-control-feedback" ng-show="field.$pending"><span us-spinner="{lines:9, length:5, width:2, radius:3, trail: 40}"></span></span>'
        };
    });

    jasifyDirectivesFormModule.directive('autofocus', ['$timeout', function ($timeout) {
        return {
            restrict: 'A',
            link: function ($scope, $element) {
                $timeout(function () {
                    $element[0].focus();
                });
            }
        };
    }]);

    jasifyDirectivesFormModule.directive('colorPicker', function () {
        return {
            restrict: 'C',
            link: function (scope, element) {

                var $element = $(element),
                    inputColorOutput = $element.closest('.cp-container').find('input.cp-value'),
                    dropdownElement = $element.closest('.dropdown');

                dropdownElement.on('hide.bs.dropdown', function () {
                    return !dropdownElement.hasClass('keepopen');
                });

                dropdownElement.hover(function () {
                    dropdownElement.addClass('keepopen');
                }, function () {
                    dropdownElement.removeClass('keepopen');
                });

                $element.farbtastic(function (color) {
                    var ngModelController = angular.element(inputColorOutput).data('$ngModelController');
                    ngModelController.$setViewValue(color);
                    inputColorOutput.val(color);
                });

                angular.element(inputColorOutput).scope().$watch(inputColorOutput.attr('ng-model'), function (newValue) {
                    $.farbtastic($element).setColor(newValue);
                });
            }
        };
    });

}(window.angular, window.jQuery));

