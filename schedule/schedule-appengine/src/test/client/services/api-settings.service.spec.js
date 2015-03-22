describe('ApiSettingsService', function () {
    var $rootScope, $q, $gapiMock, ApiSettings;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$rootScope_, Endpoint, _ApiSettings_, _$q_, _$gapiMock_) {
        $rootScope = _$rootScope_;
        ApiSettings = _ApiSettings_;
        $q = _$q_;
        $gapiMock = _$gapiMock_;

        Endpoint.jasifyLoaded();

    }));

    it("should getApiVersion", function () {
        var expected = {};
        var res = null;
        spyOn($gapiMock.client.jasify, 'apiInfo').and.returnValue($q.when({result: expected}));

        ApiSettings.getVersion().then(function (r) {
            res = r;
        });
        expect(res).toBe(null);

        $rootScope.$apply();

        expect(res).toBe(expected);
        expect($gapiMock.client.jasify.apiInfo).toHaveBeenCalled();

    });
});
