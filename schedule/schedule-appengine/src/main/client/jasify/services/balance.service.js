(function (angular) {
    angular.module('jasifyComponents').factory('Balance', balance);

    function balance(Endpoint, $q, $location) {
        var Balance = {
            createPayment: createPayment,
            cancelPayment: cancelPayment,
            executePayment: executePayment
        };

        function createPayment(request) {
            var absUrl = $location.absUrl();
            var ix = absUrl.indexOf('#');
            var baseUrl;
            if (ix == -1) {
                baseUrl = absUrl;
            } else {
                baseUrl = absUrl.substring(0, ix);
            }
            request.baseUrl = baseUrl;

            return Endpoint.jasify(function (jasify) {
                return jasify.balance.createPayment(request)
                    .then(resultHandler, errorHandler);
            });
        }

        function cancelPayment(paymentId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.cancelPayment({id: paymentId})
                    .then(resultHandler, errorHandler);
            });
        }

        function executePayment(paymentId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.executePayment(cancelPayment)
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