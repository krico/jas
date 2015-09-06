(function (angular) {

    "use strict";

    var REFERENCE_CODE_REGEXP = /^\d{27}$/;

    angular.module('jasifyComponents').directive('referenceCode', function () {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {
                ctrl.$validators.referenceCode = function (modelValue, viewValue) {
                    if (ctrl.$isEmpty(modelValue)) {
                        return true;
                    }

                    var viewValueClean = viewValue;

                    return !!REFERENCE_CODE_REGEXP.test(viewValueClean);


                };
            }
        };
    });
})(angular);