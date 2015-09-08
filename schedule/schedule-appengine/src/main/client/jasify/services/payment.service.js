(function (angular) {
    angular.module('jasifyComponents').factory('Payment', payment);

    function payment(Endpoint, $q, $location) {
        var Payment = {
            query: query,
            get: get,
            executePayment: executePayment,
            cancelPayment: cancelPayment,
            queryByReferenceCode: queryByReferenceCode,
            getPaymentInvoice: getPaymentInvoice
        };

        function query(fromDate, toDate, state) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.query({fromDate: fromDate, toDate: toDate, state: state})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function getPaymentInvoice(paymentId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.getPaymentInvoice({paymentId: paymentId})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(paymentOrId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.get({id: Endpoint.fetchId(paymentOrId)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }


        function cancelPayment(paymentOrId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.cancelPayment({id: Endpoint.fetchId(paymentOrId)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function executePayment(paymentOrId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.executePayment({id: Endpoint.fetchId(paymentOrId)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function queryByReferenceCode(referenceCode) {
            return Endpoint.jasify(function (jasify) {
                return jasify.payments.queryByReferenceCode({referenceCode: referenceCode})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        return Payment;
    }
})(angular);