describe("Application", function () {

    var $httpBackend, $rootScope;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
    }));

    beforeEach(inject(function (_$httpBackend_, _$rootScope_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

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

            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);
        });

        it("should be null after destroy", function () {

            Session.create(123, 555);
            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);
        });

    });

    describe('Auth', function () {
        var Session, Auth;
        beforeEach(inject(function (_Session_, _Auth_) {
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
            $httpBackend.flush();
            expect(user).toBeDefined();
            expect(user.id).toBe(555);
            expect(user.name).toBe('user');
            expect(user.email).toBe('user@jasify.com');

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
    });
});