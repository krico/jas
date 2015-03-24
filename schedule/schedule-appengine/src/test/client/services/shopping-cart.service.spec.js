describe('ShoppingCartService', function () {
    var ShoppingCart, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_ShoppingCart_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        ShoppingCart = _ShoppingCart_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should get shopping cart by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'get').and.returnValue($q.when({result: expected}));
        var id = 'abc';
        ShoppingCart.get(id)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.get).toHaveBeenCalledWith({id: id});
    });

    it('should get shopping cart with no id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'get').and.returnValue($q.when({result: expected}));
        ShoppingCart.get()
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.get).toHaveBeenCalledWith({});
    });

});
