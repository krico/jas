(function (angular) {
    angular.module('jasifyComponents').factory('Payment', payment);

    function payment(Endpoint, $q, $location) {
        var Payment = {
            query: query,
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


        return Payment;
    }
})(angular);