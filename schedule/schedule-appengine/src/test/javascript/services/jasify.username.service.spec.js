describe('Username', function () {
    var Username, $q, Endpoint, $gapiMock, $rootScope;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$q_, _Username_, _Endpoint_, _$gapiMock_, _$rootScope_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        Username = _Username_;
        $gapiMock = _$gapiMock_;
        Endpoint = _Endpoint_;
        Endpoint.jasifyLoaded();

    }));

    it("should tell us if username is available", function () {

        var ok = null;
        var nok = null;

        Username.check('good').then(function (res) {
                ok = true;
            },
            function (r) {
                nok = true;
            });

        $rootScope.$apply();

        expect(ok).toBe(true);
        expect(nok).toBe(null);

    });

    it("should tell us if username is unavailable", function () {
        $gapiMock.client.jasify.username.check = function () {
            return $q.reject();
        };

        var ok = null;
        var nok = null;

        Username.check('bad-name').then(function (res) {
                ok = true;
            },
            function (r) {
                nok = true;
            });

        $rootScope.$apply();

        expect(ok).toBe(null);
        expect(nok).toBe(true);
    });
});
