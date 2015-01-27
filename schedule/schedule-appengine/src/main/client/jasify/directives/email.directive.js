(function (angular) {

    angular.module('jasifyComponents').directive('jasEmail', jasEmail);

    function jasEmail($q, Unique) {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {

                ctrl.$asyncValidators.jasEmail = function (modelValue, viewValue) {

                    if (ctrl.$isEmpty(modelValue)) {
                        return $q.when();
                    }

                    var def = $q.defer();

                    return Unique.email(modelValue);
                };
            }
        };
    }
})(angular);