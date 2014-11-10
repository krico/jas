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
    /**
     * This is our backend replacement for backend-less dev :-)
     * I wonder how long we will be able to keep this up...
     *
     * Pre-defined behaviours
     *  - user.name = 'nologin'
     *    /login always (even if the password matches).  Used to test case where login fails after registration
     *
     *  - user.name = 'badsignup'
     *    /user (create user) always fails
     *
     *  - user.name = 'nocurrent'
     *    /user/current always fails
     *
     *  - user.name = 'badsave'
     *    POST /user/NNN always fails.  Test the case where profile save fails
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
            console.log("POST[username] " + url + " DATA: " + data);

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

        $httpBackend.whenPOST(/^\/logout$/).respond(function (method, url, data) {
            console.log("POST[logout] " + url + " DATA: " + data);
            database.users.current = false;
            return [200, angular.toJson({ok: true}), {}];
        });

        $httpBackend.whenPOST(/^\/login$/).respond(function (method, url, data) {
            console.log("POST[login] " + url + " DATA: " + data);

            var req = angular.fromJson(data);
            var users = database.users;
            for (var id in users) {
                var u = users[id];
                if (req.name != 'nologin' && req.name == u.name && req.password == u.password) {
                    database.users.current = u;
                    console.log('Login: set current=' + angular.toJson(u));
                    return [200, angular.toJson({ok: true}), {}];
                }
            }
            return [200 /* not found */, angular.toJson({nok: true, nokText: 'Invalid username or password.'}), {}];
        });

        $httpBackend.whenPOST(/^\/user$/).respond(function (method, url, data) {
            console.log("POST[user](CREATE) " + url + " DATA: " + data);
            /* CREATE USER */
            var user = angular.fromJson(data);

            if (user.name == 'badsignup') {
                return [200 /* bad request */, angular.toJson({
                    nok: true,
                    nokText: 'Sign up failed :-('
                }), {}];
            }

            var users = database.users;
            var max = 0;
            for (var id in users) {
                var u = users[id];
                if (u.id > max) max = u.id;
                if (user.name == u.name) {
                    return [200 /* bad request */, angular.toJson({
                        nok: true,
                        nokText: 'Username not available'
                    }), {}];
                }
            }
            user['id'] = max + 10;
            users[user.id] = user;
            return [200, angular.toJson(user), {}];
        });

        $httpBackend.whenGET(/^\/user(\/.*)$/).respond(function (method, url, data) {
            console.log("GET[user] " + url + " DATA: " + data);

            var matches = /^\/user\/(.+)$/.exec(url);
            if (matches != null) {
                var userId = matches[1];
                console.log('User: userId:' + userId);
                /* User by ID */
                if (database.users[userId]) {
                    return [200, angular.toJson(database.users[userId]), {}];
                }
            }

            return [404 /* not found */, angular.toJson({
                nok: true,
                nokText: 'No user found at: ' + url + ' :-('
            }), {}];
        });

        $httpBackend.whenPOST(/^\/user(\/.*)$/).respond(function (method, url, data) {
            console.log("POST[user] " + url + " DATA: " + data);
            var update = angular.fromJson(data);
            var matches = /^\/user\/(.+)$/.exec(url);
            if (update.name != 'badsave' && matches != null) {
                var userId = matches[1];
                console.log('User: userId:' + userId);
                /* User by ID */
                if (database.users[userId]) {
                    database.users[userId] = update;
                    return [200, angular.toJson(database.users[userId]), {}];
                }
            }

            return [404 /* not found */, angular.toJson({
                nok: true,
                nokText: 'No user found at: ' + url + ' :-('
            }), {}];
        });

        //Pass through so that gets to our partials work
        $httpBackend.whenGET(/^(\/)?views\/.*\.html$/).passThrough();
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
    }
})(angular);

