describe('HistoryService', function () {
    var History, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_History_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        History = _History_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should query all histories', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.histories, 'query').and.returnValue($q.when({result: {items: expected}}));
        History.query()
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.histories.query).toHaveBeenCalled();
    });

});
