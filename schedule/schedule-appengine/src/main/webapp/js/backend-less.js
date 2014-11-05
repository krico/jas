/**
 * Created by krico on 04/11/14.
 *
 * Enables backend-less development...
 * You need to open index.html?nobackend to get this
 */

(function (ng) {

    if (!document.URL.match(/\?nobackend(#.*)?$/)) {
        return; //standard prod
    }

    console.log('======== CUIDADO!!! USING STUBBED BACKEND ========');
    initializeStubbedBackend();

    function initializeStubbedBackend() {
        ng.module('jasifyScheduleApp')
            .config(function ($provide) {
                // decorate http with an 2e2 mock
                $provide.decorator('$httpBackend', angular.mock.e2e.$httpBackendDecorator);
                //decorate it with a timeout
                $provide.decorator('$httpBackend', function ($delegate) {
                    var proxy = function (method, url, data, callback, headers) {
                        var interceptor = function () {
                            var _this = this,
                                _arguments = arguments;
                            setTimeout(function () {
                                callback.apply(_this, _arguments);
                            }, 700);
                        };
                        return $delegate.call(this, method, url, data, interceptor, headers);
                    };
                    for (var key in $delegate) {
                        proxy[key] = $delegate[key];
                    }
                    return proxy;
                });
            })
            .run(BackendMock);

        /**
         * This is our backend replacement for backend-less dev :-)
         *
         * @param $httpBackend
         * @constructor
         */
        function BackendMock($httpBackend) {
            $httpBackend.whenPOST(/^\/username\/valid$/).respond(function (method, url, data) {
                var req = angular.fromJson(data);
                if (req.username == 'used')
                    return [200, angular.toJson({status: -1, reason: 'Username already exists'}), {}];

                return [200, angular.toJson({status: 0}), {}];
            });
            //Pass through so that gets to our partials work
            $httpBackend.whenGET(/^(\/)?views\/.*\.html$/).passThrough();
        }
    }
})(angular);

