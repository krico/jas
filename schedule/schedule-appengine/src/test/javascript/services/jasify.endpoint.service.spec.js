describe('Endpoint', function () {
    var $rootScope, Endpoint, $gapiMock;

    beforeEach(module('jasify'));

    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$rootScope_, _Endpoint_, _$gapiMock_) {
        $rootScope = _$rootScope_;
        Endpoint = _Endpoint_;
        $gapiMock = _$gapiMock_;
    }));

    it('loads proper api on init', function () {
        Endpoint.init();
        expect($gapiMock.data.load.api).toEqual('jasify');
        expect($gapiMock.data.load.version).toEqual('v1');
        expect($gapiMock.data.load.path).toEqual('/_ah/api');
        expect($gapiMock.data.load.callback).toBe(null);
        expect($gapiMock.data.load.then.success).toBeDefined();
        expect($gapiMock.data.load.then.success).toBeDefined();
        expect(angular.isFunction($gapiMock.data.load.then.success)).toBe(true);
        expect(angular.isFunction($gapiMock.data.load.then.fail)).toBe(true);
    });

    it('load returns a promise', function () {
        var loadPromise = Endpoint.load();
        expect(loadPromise.then).toBeDefined();
        expect(angular.isFunction(loadPromise.then)).toBe(true);
    });

    it('multiple loads resolve after init', function () {
        var ok1 = null;
        var fail1 = null;
        Endpoint.load().then(
            function (r) {
                ok1 = true;
            },
            function (r) {
                fail1 = true;
            }
        );
        expect(ok1).toBe(null);
        expect(fail1).toBe(null);

        var ok2 = null;
        var fail2 = null;
        Endpoint.load().then(
            function (r) {
                ok2 = true;
            },
            function (r) {
                fail2 = true;
            }
        );
        expect(ok2).toBe(null);
        expect(fail2).toBe(null);

        $rootScope.$apply(); //not resolved

        expect(ok1).toBe(null);
        expect(fail1).toBe(null);
        expect(ok2).toBe(null);
        expect(fail2).toBe(null);

        Endpoint.init();
        $gapiMock.data.load.then.success(); //TODO: test then.fail() case

        expect(ok1).toBe(null);
        expect(fail1).toBe(null);
        expect(ok2).toBe(null);
        expect(fail2).toBe(null);

        $rootScope.$apply();

        expect(ok1).toBe(true);
        expect(fail1).toBe(null);
        expect(ok2).toBe(true);
        expect(fail2).toBe(null);

        var ok3 = null;
        var fail3 = null;
        Endpoint.load().then(
            function (r) {
                ok3 = true;
            },
            function (r) {
                fail3 = true;
            }
        );

        expect(ok3).toBe(null);
        expect(fail3).toBe(null);

        $rootScope.$apply(); //resolve

        expect(ok1).toBe(true);
        expect(fail1).toBe(null);
        expect(ok2).toBe(true);
        expect(fail2).toBe(null);
        expect(ok3).toBe(true);
        expect(fail3).toBe(null);
    });

    it('passes the jasify api to the jasify call', function () {
        var captured = null;
        Endpoint.jasify(function (api) {
            captured = api;
        });
        expect(captured).toBe(null);
        Endpoint.init();
        $gapiMock.data.load.then.success();
        expect(captured).toBe(null);
        $rootScope.$apply();
        expect(captured).toBe($gapiMock.client.jasify);
    });

});
