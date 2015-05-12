(function (angular) {

    var jasifyWeb = angular.module('jasifyWeb', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ngMaterial',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.admin',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.checkout',
        'jasify.templates',
        'jasify.filters',
        'angular.filter'
    ]);

    jasifyWeb.config(function (EndpointProvider) {
        EndpointProvider.verbose(true);
    });

    /**
     * Listen to route changes and check
     */
    jasifyWeb.run(jasifyWebRun);

    function jasifyWebRun($rootScope, $log) {
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

    angular.module("template/pagination/pagination.html", []).run(["$templateCache", function ($templateCache) {
        $templateCache.put("template/pagination/pagination.html",
            "<ul class=\"pagination\">\n" +
            "  <li ng-if=\"boundaryLinks\" ng-class=\"{disabled: noPrevious()}\"><a href ng-click=\"selectPage(1)\"><i class=\"md mdi mdi-more-horiz\"></i></a></li>\n" +
            "  <li ng-if=\"directionLinks\" ng-class=\"{disabled: noPrevious()}\"><a href ng-click=\"selectPage(page - 1)\"><i class=\"md mdi mdi-chevron-left\"></i></a></li>\n" +
            "  <li ng-repeat=\"page in pages track by $index\" ng-class=\"{active: page.active}\"><a href ng-click=\"selectPage(page.number)\">{{page.text}}</a></li>\n" +
            "  <li ng-if=\"directionLinks\" ng-class=\"{disabled: noNext()}\"><a href ng-click=\"selectPage(page + 1)\"><i class=\"md mdi mdi-chevron-right\"></i></a></li>\n" +
            "  <li ng-if=\"boundaryLinks\" ng-class=\"{disabled: noNext()}\"><a href ng-click=\"selectPage(totalPages)\"><i class=\"md mdi mdi-more-horiz\"></i></a></li>\n" +
            "</ul>");
    }]);

})(angular);