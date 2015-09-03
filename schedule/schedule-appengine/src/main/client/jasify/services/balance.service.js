(function (angular) {
    angular.module('jasifyComponents').factory('Balance', balance);

    function balance(Endpoint, $q, $location) {
        var Balance = {
            createPayment: createPayment,
            createCheckoutPayment: createCheckoutPayment,
            cancelPayment: cancelPayment,
            getAccount: getAccount,
            getAccounts: getAccounts,
            getTransactions: getTransactions,
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
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function createCheckoutPayment(request) {
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
                return jasify.balance.createCheckoutPayment(request)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function cancelPayment(paymentId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.cancelPayment({id: paymentId})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function executePayment(paymentId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.executePayment(paymentId)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function getAccount() {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.getAccount()
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function getAccounts() {
            return Endpoint.jasify(function (jasify) {
                return jasify.balance.getAccounts()
                    .then(arrayHandler, Endpoint.rejectHandler);
            });
        }

        function getTransactions(accountId, limit, offset) {
            var params = {accountId: accountId};

            if (limit) params.limit = limit;

            if (offset) params.offset = offset;

            return Endpoint.jasify(function (jasify) {
                return jasify.balance.getTransactions(params)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function arrayHandler(resp) {
            resp = resp || {};
            resp.result = resp.result || {};
            resp.result.items = resp.result.items || [];
            return resp.result.items;
        }

        return Balance;
    }
})(angular);