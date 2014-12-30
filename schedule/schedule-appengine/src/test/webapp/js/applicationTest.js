describe("Application", function () {

    var $httpBackend, $rootScope, $windowMock, $gapiMock;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
        $windowMock = {
            innerHeight: 400,
            innerWidth: 500
        };
        $provide.value('$window', $windowMock);
        $gapiMock = {data: {}};
        $gapiMock.client = {};
        $gapiMock.client.load = function (api, version, callback, path) {
            $gapiMock.data.load = {
                api: api,
                version: version,
                callback: callback,
                path: path,
                then: {}
            };
            return {
                then: function (success, fail) {
                    $gapiMock.data.load.then.success = success;
                    $gapiMock.data.load.then.fail = fail;
                }
            };
        };
        $gapiMock.client.jasify = {};
        $provide.value('$gapi', $gapiMock);

    }));

    beforeEach(inject(function (_$httpBackend_, _$rootScope_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    //TODO: test routes

    describe('Session', function () {
        var Session;
        beforeEach(inject(function (_Session_) {
            Session = _Session_;
        }));

        it("should be null after instantiation", function () {

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);

        });

        it("should keep the values of create", function () {

            Session.create(123, 555);

            expect(Session.id).toBe(123);
            expect(Session.userId).toBe(555);

        });

        it("should be null after destroy", function () {

            Session.create(123, 555);
            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);

        });

    });

    describe('Endpoint', function () {
        var Endpoint;
        beforeEach(inject(function (_Endpoint_) {
            Endpoint = _Endpoint_;
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

    describe('Auth', function () {
        var Session, Auth, $cookies;
        beforeEach(inject(function (_$cookies_, _Session_, _Auth_) {
            $cookies = _$cookies_;
            Session = _Session_;
            Auth = _Auth_;
        }));

        it("should not be authenticated before login", function () {

            expect(Auth.isAuthenticated()).toBe(false);

        });

        it("should be authenticated after successful login", function () {

            expect(Auth.isAuthenticated()).toBe(false);

            var credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: credentials.name}});

            Auth.login(credentials);

            //Not flushed (e.g. authentication in progress)
            expect(Auth.isAuthenticated()).toBe(false);

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(true);
        });

        it("should be authenticated as admin after successful admin login", function () {

            expect(Auth.isAuthenticated()).toBe(false);

            var credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: credentials.name, admin: true}});

            Auth.login(credentials);

            //Not flushed (e.g. authentication in progress)
            expect(Auth.isAuthenticated()).toBe(false);
            expect(Auth.isAdmin()).toBe(false);

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(true);
            expect(Auth.isAdmin()).toBe(true);
        });

        it("should forward user on successful login", function () {

            var credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: credentials.name}});

            var user = null;
            Auth.login(credentials).then(
                //ok
                function (u) {
                    user = u;
                },
                //fail
                function () {
                });

            //Not flushed (e.g. authentication in progress)
            expect(user).toBe(null);

            $httpBackend.flush();

            expect(user).not.toBe(null);
            expect(user.id).toBe(555);
            expect(Session.userId).toBe(user.id);
        });

        it("should fail when login fails and not be authorized", function () {

            var credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', credentials)
                .respond(401 /* Unauthorized */);

            var succeeded = false;
            var failed = false;
            Auth.login(credentials).then(
                //ok
                function (u) {
                    succeeded = true;
                },
                //fail
                function () {
                    failed = true;
                });

            //Not flushed (e.g. authentication in progress)
            expect(succeeded).toBe(false);
            expect(failed).toBe(false);

            $httpBackend.flush();

            expect(succeeded).toBe(false);
            expect(failed).toBe(true);
            expect(Auth.isAuthenticated()).toBe(false);

        });

        it("should change password", function () {

            var credentials = {name: 'test', password: 'password'};
            $httpBackend.expectPOST('/auth/login', credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: credentials.name}});


            Auth.login(credentials);

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(true);

            $httpBackend.expectPOST('/auth/change-password', {
                credentials: credentials,
                newPassword: 'newPw'
            }).respond(200);

            var ok = false;

            Auth.changePassword(credentials, 'newPw').then(function () {
                ok = true;
            });

            expect(ok).toBe(false);

            $httpBackend.flush();

            expect(ok).toBe(true);
        });

        it("should restore an existing session", function () {

            $httpBackend.expectGET('/auth/restore')
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: 'test'}});

            var user = null;

            $cookies.loggedIn = true;

            Auth.restore().then(function (u) {
                user = u;
            });

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(true);
            expect(user).not.toBe(null);
            expect(user.id).toBe(555);
            expect(Session.userId).toBe(user.id);
        });

        it("should detect there is no session to restore", function () {

            $httpBackend.expectGET('/auth/restore').respond(401);

            var user = null;
            var failed = false;

            $cookies.loggedIn = true;

            Auth.restore().then(function (u) {
                    user = u;
                },
                function () {
                    failed = true;
                });

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(false);
            expect(user).toBe(null);
            expect(failed).toBe(true);
        });

        it("should not restore again when session existed", function () {

            $httpBackend.expectGET('/auth/restore')
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: 'test'}});

            var user = null;

            $cookies.loggedIn = true;

            Auth.restore().then(function (u) {
                user = u;
            });

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(true);
            expect(user).not.toBe(null);
            expect(user.id).toBe(555);
            expect(Session.userId).toBe(user.id);

            user = null;

            Auth.restore().then(function (u) {
                user = u;
            });

            //propagate promise resolution (damn I lost a lot of time on this one!)
            $rootScope.$apply();

            expect(Auth.isAuthenticated()).toBe(true);
            expect(user).not.toBe(null);
            expect(user.id).toBe(555);
            expect(Session.userId).toBe(user.id);

        });

        it("should not restore again when session is not available", function () {

            $httpBackend.expectGET('/auth/restore').respond(401);

            var user = null;
            var failed = false;

            $cookies.loggedIn = true;

            Auth.restore().then(function (u) {
                    user = u;
                },
                function (why) {
                    failed = true;
                });

            $httpBackend.flush();

            expect(Auth.isAuthenticated()).toBe(false);
            expect(user).toBe(null);
            expect(failed).toBe(true);

            failed = false;

            Auth.restore().then(function (u) {
                    user = u;
                },
                function () {
                    failed = true;
                });

            $rootScope.$apply();

            expect(Auth.isAuthenticated()).toBe(false);
            expect(user).toBe(null);
            expect(failed).toBe(true);
        });

    });

    describe('Allow', function () {
        var Allow, Session, AUTH_EVENTS;

        beforeEach(inject(function (_Allow_, _Session_, _AUTH_EVENTS_) {
            Allow = _Allow_;
            Session = _Session_;
            AUTH_EVENTS = _AUTH_EVENTS_;
        }));

        it("all: should promise true", function () {
            var ok = null;
            Allow.all().then(function () {
                ok = true;
            });
            expect(ok).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(true);
        });

        it("guest: should promise true when not authenticated", function () {
            var ok = null;
            var fail = null;
            Allow.guest().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(true);
            expect(fail).toBe(null);
        });

        it("guest: should promise false when authenticated", function () {

            var ok = null;
            var fail = null;

            Session.create(1, 1); //make him authenticated

            spyOn($rootScope, '$broadcast');
            Allow.guest().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(null);
            expect(fail).toBe(true);
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.notGuest);
        });

        it("user: should promise false when not authenticated", function () {
            var ok = null;
            var fail = null;
            spyOn($rootScope, '$broadcast');
            Allow.user().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(null);
            expect(fail).toBe(true);
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.notAuthenticated);
        });

        it("user: should promise true when authenticated", function () {
            var ok = null;
            var fail = null;

            Session.create(1, 1); //make him authenticated

            Allow.user().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(true);
            expect(fail).toBe(null);
        });

        it("admin: should promise false when not authenticated", function () {
            var ok = null;
            var fail = null;
            spyOn($rootScope, '$broadcast');
            Allow.admin().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(null);
            expect(fail).toBe(true);
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.notAuthenticated);
        });

        it("admin: should promise false when authenticated as user", function () {
            var ok = null;
            var fail = null;

            Session.create(1, 1); //make him authenticated
            spyOn($rootScope, '$broadcast');
            Allow.admin().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(null);
            expect(fail).toBe(true);
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.notAuthorized);
        });

        it("admin: should promise true when authenticated as admin", function () {
            var ok = null;
            var fail = null;

            Session.create(1, 1, true); //make him authenticated

            Allow.admin().then(function () {
                ok = true;
            }, function () {
                fail = true;
            });
            expect(ok).toBe(null);
            expect(fail).toBe(null);
            $rootScope.$apply();
            expect(ok).toBe(true);
            expect(fail).toBe(null);
        });
    });

    describe('Username', function () {
        var Username;

        beforeEach(inject(function (_Username_) {
            Username = _Username_;
        }));

        it("should tell us if username is available", function () {
            $httpBackend.expectPOST('/username', 'good').respond(200);

            var ok = null;
            var nok = null;

            Username.check('good').then(function (res) {
                    ok = true;
                },
                function (r) {
                    nok = true;
                });

            $httpBackend.flush();

            expect(ok).toBe(true);
            expect(nok).toBe(null);

        });

        it("should tell us if username is unavailable", function () {
            $httpBackend.expectPOST('/username', 'bad-name').respond(406);


            var ok = null;
            var nok = null;

            Username.check('bad-name').then(function (res) {
                    ok = true;
                },
                function (r) {
                    nok = true;
                });

            $httpBackend.flush();

            expect(ok).toBe(null);
            expect(nok).toBe(true);
        });
    });

    describe('User', function () {
        var User;

        beforeEach(inject(function (_User_) {
            User = _User_;
        }));

        it('should get a user by id', function () {

            $httpBackend.expectGET('/user/555').respond(200, {id: 555, name: 'user', email: 'user@jasify.com'});
            var user = User.get({id: 555});

            expect(user instanceof User).toBe(true);

            $httpBackend.flush();

            expect(user instanceof User).toBe(true);

            expect(user).toBeDefined();
            expect(user.id).toBe(555);
            expect(user.name).toBe('user');
            expect(user.email).toBe('user@jasify.com');

        });

        it('should handle forbidden get by id', function () {

            $httpBackend.expectGET('/user/555').respond(403 /* forbidden */);
            var succeeded = null;
            var failed = null;
            var user = User.get({id: 555},
                function (r) {
                    succeeded = false;
                },
                function (r) {
                    failed = true;
                });

            expect(user instanceof User).toBe(true);

            $httpBackend.flush();

            expect(user instanceof User).toBe(true);

            expect(succeeded).toBe(null);

            expect(failed).toBe(true);

        });

        it('should save an existing user', function () {

            $httpBackend.expectGET('/user/555').respond(200, {id: 555, name: 'user', email: 'user@jasify.com'});
            var user = User.get({id: 555});
            $httpBackend.flush();

            $httpBackend.expectPOST('/user/555', {id: 555, name: 'user', email: 'user2@jasify.com'})
                .respond(200, {id: 555, name: 'user', email: 'user2@jasify.com'});

            user.email = 'user2@jasify.com';

            user.$save();

            $httpBackend.flush();

            expect(user).toBeDefined();
            expect(user.id).toBe(555);
            expect(user.name).toBe('user');
            expect(user.email).toBe('user2@jasify.com');

        });

        it('should query and return an array', function () {

            $httpBackend.expectGET('/user').respond(200, [{id: 555, name: 'user', email: 'user@jasify.com'}]);
            var users = User.query();
            $httpBackend.flush();

            expect(users instanceof Array).toBe(true);
            expect(users[0]).toBeDefined();
            expect(users[0].id).toBe(555);
            expect(users[0].name).toBe('user');
            expect(users[0].email).toBe('user@jasify.com');

        });

        it('should query with parameters', function () {

            var q = {page: 1, size: 5, sort: 'asc', field: 'email', query: 'something@'};
            $httpBackend.expectGET(function (url) {
                if (url.indexOf('/user?') != 0) return false;
                for (p in q) {
                    var re = new RegExp(p + '=' + q[p]);
                    if (!url.match(re)) {
                        console.log('re=' + re);
                        return false;
                    }
                }
                return true;
            }).respond(200, [{id: 555, name: 'user', email: 'user@jasify.com'}]);
            var users = User.query(q);
            $httpBackend.flush();

            expect(users instanceof Array).toBe(true);
            expect(users[0]).toBeDefined();
            expect(users[0].id).toBe(555);
            expect(users[0].name).toBe('user');
            expect(users[0].email).toBe('user@jasify.com');

        });
    });

    describe('UserLogin', function () {
        var UserLogin, Endpoint;

        beforeEach(inject(function (_UserLogin_, _Endpoint_) {
            UserLogin = _UserLogin_;
            Endpoint = _Endpoint_;
            Endpoint.jasifyLoaded();
            $gapiMock.client.jasify.userLogins = {};
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
                return {result: {items: expected}}
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
            var expectedUserId = 123;
            var expectedLoginId = 123;
            $gapiMock.client.jasify.userLogins.remove = function (params) {
                ++calls;
                captureParams = params;
                return {result: {}}
            };

            UserLogin.remove(expectedUserId, {id: {id: expectedLoginId}}).then(function (result) {
                captureResult = result;
            });

            expect(captureParams).toBe(null);
            expect(captureResult).toBe(null);
            expect(calls).toEqual(0);

            $rootScope.$apply();

            expect(captureParams).toEqual({userId: expectedUserId, loginId: expectedLoginId});
            expect(calls).toEqual(1);

        });
    });

    describe('Popup', function () {
        var Popup, $interval;

        beforeEach(inject(function (_$interval_, _Popup_) {
            $interval = _$interval_;
            Popup = _Popup_;
        }));

        it('build options with no args', function () {
            var opts = Popup.getOptions();
            expect(opts.width).toEqual(500);
            expect(opts.height).toEqual(500);
        });

        it('build options with args', function () {
            var opts = Popup.getOptions({width: 600});
            expect(opts.width).toEqual(600);
            expect(opts.height).toEqual(500);
        });

        it('build a string with options', function () {
            var str = Popup.optionsString({foo: 'bar', baz: 90});
            expect(str).toEqual('foo=bar,baz=90');
        });

        it('opens a popup window for auth', function () {
            var ret = {};
            var param = {x: null, y: null, z: null};
            $windowMock.open = function (x, y, z) {
                param.x = x;
                param.y = y;
                param.z = z;
                return ret;
            };
            var ok = null;
            var fail = null;
            Popup.open('testUrl', 'Facebook')
                .then(
                function () {
                    ok = true;
                },
                function () {
                    fail = true;
                });
            expect(param.x).toEqual('testUrl');
            expect(param.y).toEqual('_blank');
            expect(param.z.indexOf('height=269')).not.toEqual(-1);

            ret.closed = true;

            $interval.flush(60);

            expect(ok).toBe(null);
            expect(fail).toBe(true);
        });

    });
});