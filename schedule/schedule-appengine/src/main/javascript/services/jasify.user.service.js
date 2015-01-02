(function () {

    /**
     * User service
     */
    angular.module('jasify').factory('User', user);

    function user($resource) {
        return $resource('/user/:id', {id: '@id'});
    }

})();