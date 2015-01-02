(function (ng) {

    /**
     * User service
     */
    ng.module('jasify').factory('User', user);

    function user($resource) {
        return $resource('/user/:id', {id: '@id'});
    }

})(angular);