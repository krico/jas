(function (angular) {

    /**
     * Version information (values are replaced by gulp build)
     */
    angular.module('jasifyComponents').constant('VERSION', {
        number: '@NUMBER@',
        branch: '@BRANCH@',
        timestamp: '@TIMESTAMP@',
        version: '@VERSION@'
    });
})(angular);