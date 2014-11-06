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
         * I wonder how long we will be able to keep this up...
         *
         * @param $httpBackend
         * @constructor
         */
        function BackendMock($httpBackend) {
            /* The backend-less database */
            var database = {
                users: {
                    /* userId : {userData} */
                    100: {
                        id: 100,
                        name: 'krico',
                        password: 'krico'
                    }
                }
            };

            /**
             * Username check
             */
            $httpBackend.whenPOST(/^\/username$/).respond(function (method, url, data) {
                var name = data;
                var users = database.users;
                for (var id in users) {
                    var user = users[id];
                    if (user.name == name) {
                        //User exists
                        return [200, angular.toJson({
                            nok: true,
                            nokText: 'Username not available'
                        }), {}];
                    }
                }
                return [200, angular.toJson({ok: true}), {}];
            });

            /**
             * User RESTful CRUD operations
             */
            $httpBackend.whenPOST(/^\/user(\/.*)?$/).respond(function (method, url, data) {
                console.log("POST " + url + " DATA: " + data);

                if (url = ~/^\/user(\/)?$/) {
                    var user = angular.fromJson(data);
                    var users = database.users;
                    var max = 0;
                    for (var id in users) {
                        var u = users[id];
                        if (u.id > max) max = u.id;
                        if (user.name == u.name) {
                            //todo: whats status case for error?
                            return [404, angular.toJson({
                                nok: true,
                                nokText: 'Username not available'
                            }), {}];
                        }
                    }
                    user['id'] = max + 10;
                    users[user.id] = user;
                    return [200, angular.toJson(user), {}];
                }
                //TODO: what's status code for not found or no such method
                return [404, angular.toJson({
                    nok: true,
                    nokText: 'Username not available'
                }), {}];
            });
            //Pass through so that gets to our partials work
            $httpBackend.whenGET(/^(\/)?views\/.*\.html$/).passThrough();
        }
    }
})(angular);

