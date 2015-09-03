describe('PaymentService', function () {
    var Payment, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_Payment_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        Payment = _Payment_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should call getPaymentInvoice', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.payments, 'getPaymentInvoice').and.returnValue($q.when({result: expected}));
        Payment.getPaymentInvoice('pId')
            .then(function (ret) {
                expect(ret).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.getPaymentInvoice).toHaveBeenCalledWith({paymentId: 'pId'});
    });

    it('should query all payments', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.payments, 'query').and.returnValue($q.when({result: {items: expected}}));
        var from = 1;
        var to = 2;
        Payment.query(from, to)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.query).toHaveBeenCalledWith({fromDate: from, toDate: to});
    });

});