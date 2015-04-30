(function (angular, $) {
    var jasifyDirectivesFormModule = angular.module('jasify.directives.form', []);

    jasifyDirectivesFormModule.run(function() {
        $('body').on('focus', '.form-control', function(){
            $(this).closest('.fg-line').addClass('fg-toggled');
        });

        $('body').on('blur', '.form-control', function(){
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

        $('body').on('click', '.sub-menu > a', function(e){
            e.preventDefault();
            $(this).next().slideToggle(200);
            $(this).parent().toggleClass('toggled');
        });
    });

    jasifyDirectivesFormModule.directive('prevent', function() {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {
                elem.on(attrs.prevent, function(e){
                    e.preventDefault();
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('fgInput', function() {
        return {
            restrict: 'C',
            link: function(scope, element, attrs, controllers) {
                scope.$watch(attrs.ngModel, function(newValue) {
                    if (!!newValue) {
                        $(element).parent('.fg-line').addClass('fg-toggled');
                    }
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('fg-float', function() {
        return {
            restrict: 'C',
            link: function(scope, element, attrs) {
                console.log(element);
                $('.form-control', element).each(function(){
                    var i = $(this).val();

                    if (i.length !== 0) {
                        $(this).closest('.fg-line').addClass('fg-toggled');
                    }
                });
            }
        };
    });

    jasifyDirectivesFormModule.directive('jasErrorHelpBlock', function() {
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

    jasifyDirectivesFormModule.directive('jasHasFeedback', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                var $element = $(element);

                var formName = $element.closest('form').attr('name');
                var formFieldName = $element.find('input, select, textarea').attr('name');

                scope.$watch(formName + '.' + formFieldName + '.$touched', function() {
                    updateClasses();
                });

                scope.$watch(formName + '.' + formFieldName + '.$valid', function() {
                    updateClasses();
                });

                scope.$watch(formName + '.$submitted', function() {
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

    jasifyDirectivesFormModule.directive('jasFeedbackIconValid', function() {
        return {
            multiElement: true,
            restrict: 'E',
            replace: true,
            scope: {
                field: '='
            },
            template: '<i ng-show="field.$valid && field.$touched" class="md-icon md-icon-check form-control-feedback"></i>'
        };
    });

    jasifyDirectivesFormModule.directive('jasFeedbackIconInvalid', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                field: '=',
                form: '='
            },
            template: '<i ng-show="(form.$submitted || field.$touched) && field.$invalid" class="md-icon md-icon-close form-control-feedback"></i>'
        };
    });

    jasifyDirectivesFormModule.directive('jasFeedbackIconPending', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                field: '='
            },
            template: '<span class="form-control-feedback" ng-show="field.$pending"><span us-spinner="{lines:9, length:5, width:2, radius:3, trail: 40}"></span></span>'
        };
    });

}(angular, jQuery));

