describe('BalanceService', function () {
    var Balance, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_Balance_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        Balance = _Balance_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should call createPayment', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.balance, 'createPayment').and.returnValue($q.when({result: {items: expected}}));
        Balance.createPayment({})
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.balance.createPayment).toHaveBeenCalled();
    });

    it('should call cancelPayment', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.balance, 'cancelPayment').and.returnValue($q.when({result: {items: expected}}));
        var pid = 22;

        Balance.cancelPayment(pid)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.balance.cancelPayment).toHaveBeenCalledWith({id: pid});
    });

    it('should call executePayment', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.balance, 'executePayment').and.returnValue($q.when({result: {items: expected}}));
        Balance.executePayment()
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.balance.executePayment).toHaveBeenCalled();
    });
});