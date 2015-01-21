describe('UniqueService', function () {
    var Unique, $q, Endpoint, $gapiMock, $rootScope;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$q_, _Unique_, _Endpoint_, _$gapiMock_, _$rootScope_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        Unique = _Unique_;
        $gapiMock = _$gapiMock_;
        Endpoint = _Endpoint_;
        Endpoint.jasifyLoaded();

    }));

    it("should tell us if username is available", function () {

        var ok = null;
        var nok = null;

        Unique.username('good').then(function (res) {
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
        $gapiMock.client.jasify.unique.username = function () {
            return $q.reject();
        };

        var ok = null;
        var nok = null;

        Unique.username('bad-name').then(function (res) {
                ok = true;
            },
            function (r) {
                nok = true;
            });

        $rootScope.$apply();

        expect(ok).toBe(null);
        expect(nok).toBe(true);
    });

    it("should tell us if email is available", function () {

        var ok = null;
        var nok = null;

        Unique.email('good').then(function (res) {
                ok = true;
            },
            function (r) {
                nok = true;
            });

        $rootScope.$apply();

        expect(ok).toBe(true);
        expect(nok).toBe(null);

    });

    it("should tell us if email is unavailable", function () {
        $gapiMock.client.jasify.unique.email = function () {
            return $q.reject();
        };

        var ok = null;
        var nok = null;

        Unique.email('bad-name').then(function (res) {
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
