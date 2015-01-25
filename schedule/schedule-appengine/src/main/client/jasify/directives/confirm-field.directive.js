(function (angular) {

    /**
     * ConfirmField directive
     */
    angular.module('jasifyComponents').directive('jasConfirmField', jasConfirmField);

    function jasConfirmField() {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {
                scope.$watch(function () {
                        var compareTo = scope.$eval(attrs.jasConfirmField);
                        return compareTo && compareTo.$viewValue;
                    },
                    function (newValue, oldValue) {
                        if (ctrl.$pristine) {
                            return;
                        }
                        if (ctrl.$modelValue == newValue) {
                            return;
                        }
                        ctrl.$validate();
                    });

                ctrl.$validators.jasConfirmField = function (modelValue, viewValue) {
                    var compareTo = scope.$eval(attrs.jasConfirmField);
                    if (compareTo && compareTo.$modelValue !== null && modelValue != compareTo.$modelValue) {
                        return false;
                    }
                    return compareTo && compareTo.$modelValue !== null;
                };
            }
        };
    }

})(angular);