describe('EndpointProvider', function () {
    var $rootScope, Endpoint, $gapiMock;

    function checkInitCall(api, version, path) {
        Endpoint.init();
        expect($gapiMock.data.load.api).toEqual(api);
        expect($gapiMock.data.load.version).toEqual(version);
        expect($gapiMock.data.load.path).toEqual(path);
        expect($gapiMock.data.load.callback).toBe(null);
        expect($gapiMock.data.load.then.success).toBeDefined();
        expect($gapiMock.data.load.then.success).toBeDefined();
        expect(angular.isFunction($gapiMock.data.load.then.success)).toBe(true);
        expect(angular.isFunction($gapiMock.data.load.then.fail)).toBe(true);
    }

    describe('Service', function () {

        beforeEach(module('jasifyComponents'));

        beforeEach(module('jasify.mocks'));

        beforeEach(inject(function (_$rootScope_, _Endpoint_, _$gapiMock_) {
            $rootScope = _$rootScope_;
            Endpoint = _Endpoint_;
            $gapiMock = _$gapiMock_;
        }));

        it('loads proper api on init', function () {
            checkInitCall('jasify', 'v1', '/_ah/api');
        });

        it('has the correct google client url by default', function () {
            expect(Endpoint.googleClientUrl).toEqual('https://apis.google.com/js/client.js');
        });

        it('fetch the id of an object', function () {
            expect(Endpoint.fetchId({id: 'kkk'})).toEqual('kkk');
        });

        it('return the id of a non object', function () {
            expect(Endpoint.fetchId('kkk')).toEqual('kkk');
        });

        it('should transform undefined response', function () {
            expect(Endpoint.resultHandler()).toBeUndefined();
        });

        it('should not transform proper response', function () {
            expect(Endpoint.resultHandler({result: 'abc'})).toEqual('abc');
        });

        it('should transform undefined items response', function () {
            expect(Endpoint.itemsResultHandler()).toEqual({items: []});
        });

        it('should not transform proper items response', function () {
            var expected = {result: {items: ['a']}};
            expect(Endpoint.itemsResultHandler(expected)).toEqual(expected.result);
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

    describe('Config', function () {
        beforeEach(module('jasifyComponents', function (EndpointProvider) {
            EndpointProvider.apiName('myApiName');
            EndpointProvider.apiVersion('myApiVersion');
            EndpointProvider.apiPath('https://my/api/path');
            EndpointProvider.googleClientUrl('https://my/google/client.js');
        }));

        beforeEach(module('jasify.mocks'));

        beforeEach(inject(function (_$rootScope_, _Endpoint_, _$gapiMock_) {
            $rootScope = _$rootScope_;
            Endpoint = _Endpoint_;
            $gapiMock = _$gapiMock_;
        }));

        it('uses values configured on provider', function () {
            checkInitCall('myApiName', 'myApiVersion', 'https://my/api/path');
            expect(Endpoint.googleClientUrl).toEqual('https://my/google/client.js');
        });

        it('uses configured google client url', function () {
            expect(Endpoint.googleClientUrl).toEqual('https://my/google/client.js');
        });

    });

});
