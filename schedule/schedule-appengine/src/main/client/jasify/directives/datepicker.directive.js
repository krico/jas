(function (angular) {

    // TODO: This should be jasDatepickerPopup
    angular.module('jasifyComponents').directive('datepickerPopup', datepickerPopup);

    function datepickerPopup () {
        return {
            restrict: 'EAC',
            require: 'ngModel',
            link: function (scope, element, attr, controller) {
                //remove the default formatter from the input directive to prevent conflict
                controller.$formatters.shift();
            }
        };
    }

})(angular);