(function (angular) {
    angular.module('jasifyComponents').factory('Balance', balance);

    function balance(Endpoint, $q, $log) {
        var Balance = {
            createPayment: createPayment
        };

        function createPayment(request) {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.createPayment(request)
                    .then(resultHandler, errorHandler);
            });
        }

        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
        }

        return Balance;
    }
})(angular);