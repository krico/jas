(function (angular) {

    angular.module('jasify').directive('jasEmail', jasEmail);

    function jasEmail($q, Unique) {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {

                ctrl.$asyncValidators.email = function (modelValue, viewValue) {

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