(function (angular) {


    angular.module('jasifyComponents').factory('Unique', unique);

    function unique(Endpoint) {
        var Unique = {
            username: username,
            email: email
        };

        function username(name) {
            return Endpoint.jasify(function (jasify) {
                return jasify.unique.username({username: name});
            });
        }

        function email(value) {
            return Endpoint.jasify(function (jasify) {
                return jasify.unique.email({email: value});
            });
        }

        return Unique;
    }

})(angular);