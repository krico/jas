(function () {


    angular.module('jasify').factory('Username', username);
    function username($log, Endpoint) {
        var Username = {
            check: check
        };

        function check(name) {
            return Endpoint.jasify(function (jasify) {
                return jasify.username.check({username: name});
            });
        }

        return Username;
    }

})();