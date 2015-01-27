(function (angular) {

    angular.module('jasify').directive('jasUsername', jasUsername);

    function jasUsername($q, Unique) {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {

                ctrl.$asyncValidators.username = function (modelValue, viewValue) {

                    if (ctrl.$isEmpty(modelValue)) {
                        return $q.when();
                    }

                    var def = $q.defer();

                    return Unique.username(modelValue);
                };
            }
        };
    }
})(angular);