(function (angular) {

    angular.module('jasify').directive('jasUsername', jasUsername);

    function jasUsername($q, Username) {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {

                ctrl.$asyncValidators.username = function (modelValue, viewValue) {

                    if (ctrl.$isEmpty(modelValue)) {
                        return $q.when();
                    }

                    var def = $q.defer();

                    return Username.check(modelValue);
                };
            }
        };
    }
})(angular);