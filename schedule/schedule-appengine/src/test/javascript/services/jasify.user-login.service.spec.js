describe('UserLoginService', function () {
    var UserLogin, Endpoint, $gapiMock, $rootScope;
    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));
    beforeEach(inject(function (_UserLogin_, _Endpoint_, _$gapiMock_, _$rootScope_) {
        UserLogin = _UserLogin_;
        Endpoint = _Endpoint_;
        $gapiMock = _$gapiMock_;
        $rootScope = _$rootScope_;
        Endpoint.jasifyLoaded();
    }));

    it('list calls correct api method with correct parameters', function () {
        var calls = 0;
        var captureParams = null;
        var captureResult = null;
        var expected = [];
        var expectedUserId = 123;
        $gapiMock.client.jasify.userLogins.list = function (params) {
            ++calls;
            captureParams = params;
            return {result: {items: expected}};
        };

        UserLogin.list(expectedUserId).then(function (result) {
            captureResult = result;
        });

        expect(captureParams).toBe(null);
        expect(captureResult).toBe(null);
        expect(calls).toEqual(0);

        $rootScope.$apply();

        expect(captureParams).toEqual({userId: expectedUserId});
        expect(captureResult).toBe(expected);
        expect(calls).toEqual(1);

    });
    it('remove calls correct api method with correct parameters', function () {
        var calls = 0;
        var captureParams = null;
        var captureResult = null;
        var expectedLoginId = 123;
        $gapiMock.client.jasify.userLogins.remove = function (params) {
            ++calls;
            captureParams = params;
            return {result: {}};
        };

        UserLogin.remove({id: expectedLoginId}).then(function (result) {
            captureResult = result;
        });

        expect(captureParams).toBe(null);
        expect(captureResult).toBe(null);
        expect(calls).toEqual(0);

        $rootScope.$apply();

        expect(captureParams).toEqual({loginId: expectedLoginId});
        expect(calls).toEqual(1);

    });
});
