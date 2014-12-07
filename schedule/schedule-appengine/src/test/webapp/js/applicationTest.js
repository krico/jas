describe("Application", function () {

    var $httpBackend;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
    }));

    beforeEach(inject(function (_$httpBackend_) {
        $httpBackend = _$httpBackend_;
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

        it("Should be null after instantiation", function () {

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);

        });

        it("Should keep the values of create", function () {

            Session.create(123, 555);

            expect(Session.id).toBe(123);
            expect(Session.userId).toBe(555);

            Session.destroy();

            expect(Session.id).toBe(null);
            expect(Session.userId).toBe(null);
        });

        it("Should be null after destroy", function () {

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

        it("Should not be authenticated before login", function () {

            expect(Auth.isAuthenticated()).toBe(false);

        });

        it("Should be authenticated after successful login", function () {

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

        it("Should forward user on successful login", function () {

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

        it("Should fail when login fails and not be authorized", function () {

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

    });
});