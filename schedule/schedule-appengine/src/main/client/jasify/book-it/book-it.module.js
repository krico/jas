(function (angular) {
    angular.module('jasify.bookIt', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.templates'
    ]);
})(angular);