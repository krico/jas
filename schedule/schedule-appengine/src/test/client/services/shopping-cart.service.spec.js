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

    it('should get user shopping cart ', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'getUserCart').and.returnValue($q.when({result: expected}));
        ShoppingCart.getUserCart()
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.getUserCart).toHaveBeenCalled();
    });

    it('should remove item from cart by id (objects)', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'removeItem').and.returnValue($q.when({result: expected}));
        var cart = {id: "foo"};
        var item = {ordinal: "bar"};
        ShoppingCart.removeItem(cart, item)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.removeItem).toHaveBeenCalledWith({cartId: cart.id, ordinal: item.ordinal});
    });

    it('should remove item from cart by id (ids)', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'removeItem').and.returnValue($q.when({result: expected}));
        var cart = {id: "foo"};
        var item = {ordinal: "bar"};
        ShoppingCart.removeItem(cart.id, item.ordinal)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.removeItem).toHaveBeenCalledWith({cartId: cart.id, ordinal: item.ordinal});
    });

    it('should remove item from cart by id (ids zero ordinal)', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'removeItem').and.returnValue($q.when({result: expected}));
        var cart = {id: 1};
        var item = {ordinal: 0};
        ShoppingCart.removeItem(cart.id, item.ordinal)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.removeItem).toHaveBeenCalledWith({cartId: 1, ordinal: 0});
    });

    it('should remove item from cart by id (ids zero cart)', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'removeItem').and.returnValue($q.when({result: expected}));
        var cart = {id: 0};
        var item = {ordinal: 1};
        ShoppingCart.removeItem(cart.id, item.ordinal)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.removeItem).toHaveBeenCalledWith({cartId: 0, ordinal: 1});
    });

    it('should add user activity to cart', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'addUserActivity').and.returnValue($q.when({result: expected}));
        var activity = {id: 0};
        ShoppingCart.addUserActivity(activity)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.addUserActivity).toHaveBeenCalledWith({activityId: activity.id});
    });

    it('should add user activity to cart by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.carts, 'addUserActivity').and.returnValue($q.when({result: expected}));
        var activity = {id: 0};
        ShoppingCart.addUserActivity(activity.id)
            .then(function (res) {
                expect(res).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.carts.addUserActivity).toHaveBeenCalledWith({activityId: activity.id});
    });

});
