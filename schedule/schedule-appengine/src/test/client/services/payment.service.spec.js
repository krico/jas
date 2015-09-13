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
        var state = 'New';
        Payment.query(from, to, state)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.query).toHaveBeenCalledWith({fromDate: from, toDate: to, state: state});
    });

    it('should get payment by id', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'get').and.returnValue($q.when({result: expected}));
        Payment.get(expected.id)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.get).toHaveBeenCalledWith({id: expected.id});
    });

    it('should get payment by object', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'get').and.returnValue($q.when({result: expected}));
        Payment.get(expected)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.get).toHaveBeenCalledWith({id: expected.id});
    });

    it('should get payment by referenceCode', function () {
        var expected = [{id: 'foo', state: 'New'}];
        spyOn($gapiMock.client.jasify.payments, 'queryByReferenceCode').and.returnValue($q.when({result: {items: expected}}));
        var referenceCode = 'refCod';
        Payment.queryByReferenceCode(referenceCode)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.queryByReferenceCode).toHaveBeenCalledWith({referenceCode: referenceCode});
    });

    it('should execute payment by id', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'executePayment').and.returnValue($q.when({result: expected}));
        Payment.executePayment(expected.id)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.executePayment).toHaveBeenCalledWith({id: expected.id});
    });

    it('should execute payment by object', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'executePayment').and.returnValue($q.when({result: expected}));
        Payment.executePayment(expected)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.executePayment).toHaveBeenCalledWith({id: expected.id});
    });

    it('should cancel payment by id', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'cancelPayment').and.returnValue($q.when({result: expected}));
        Payment.cancelPayment(expected.id)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.cancelPayment).toHaveBeenCalledWith({id: expected.id});
    });

    it('should cancel payment by object', function () {
        var expected = {id: 'foo', state: 'New'};
        spyOn($gapiMock.client.jasify.payments, 'cancelPayment').and.returnValue($q.when({result: expected}));
        Payment.cancelPayment(expected)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.payments.cancelPayment).toHaveBeenCalledWith({id: expected.id});
    });
});