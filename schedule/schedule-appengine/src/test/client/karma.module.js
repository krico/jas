(function (angular) {

    /*
        Because test are always loading jasifyWeb, there's a need to override normal configuration
        (eg. angular-translate configuration that causes GET).

        New module jasifyWebTest is loaded instead of jasifyWeb
     */
    'use strict';

    var jasifyWebTest = angular.module('jasifyWebTest', ['jasifyWeb']);

    jasifyWebTest.config(function ($provide, $translateProvider) {
        $provide.factory('customLoader', function ($q) {
            return function () {
                var deferred = $q.defer();
                deferred.resolve({});
                return deferred.promise;
            };
        });
        $translateProvider.useLoader('customLoader');
    });

})(window.angular);