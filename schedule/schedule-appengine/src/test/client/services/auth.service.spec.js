describe('AuthService', function () {
    var Session, Auth, $cookies, Endpoint, $q, $httpBackend, $rootScope, $gapiMock;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$cookies_, _Session_, _Auth_, _Endpoint_, _$q_, _$httpBackend_, _$rootScope_, _$gapiMock_) {
        $cookies = _$cookies_;
        Session = _Session_;
        Auth = _Auth_;
        $q = _$q_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        Endpoint = _Endpoint_;
        $gapiMock = _$gapiMock_;
        Endpoint.jasifyLoaded();
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it("should not be authenticated before login", function () {

        expect(Auth.isAuthenticated()).toBe(false);

    });

    it("should be authenticated after successful login", function () {

        expect(Auth.isAuthenticated()).toBe(false);

        var credentials = {name: 'test', password: 'password'};

        spyOn($gapiMock.client.jasify.auth, 'login')
            .and.returnValue({result: {userId: 555, sessionId: "b", name: credentials.name}});

        Auth.login(credentials);

        //Not flushed (e.g. authentication in progress)
        expect(Auth.isAuthenticated()).toBe(false);

        $rootScope.$apply();

        expect(Auth.isAuthenticated()).toBe(true);
        expect($gapiMock.client.jasify.auth.login)
            .toHaveBeenCalledWith({username: credentials.name, password: credentials.password});
    });

    it("should be authenticated as admin after successful admin login", function () {

        expect(Auth.isAuthenticated()).toBe(false);

        var credentials = {name: 'test', password: 'password'};

        spyOn($gapiMock.client.jasify.auth, 'login')
            .and.returnValue({result: {userId: 555, sessionId: "b", name: credentials.name, admin: true}});

        Auth.login(credentials);

        //Not flushed (e.g. authentication in progress)
        expect(Auth.isAuthenticated()).toBe(false);
        expect(Auth.isAdmin()).toBe(false);

        $rootScope.$apply();

        expect(Auth.isAuthenticated()).toBe(true);
        expect(Auth.isAdmin()).toBe(true);
        expect($gapiMock.client.jasify.auth.login)
            .toHaveBeenCalledWith({username: credentials.name, password: credentials.password});
    });

    it("should forward user on successful login", function () {

        var credentials = {name: 'test', password: 'password'};

        spyOn($gapiMock.client.jasify.auth, 'login')
            .and.returnValue({result: {userId: 555, sessionId: "b", name: credentials.name}});


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

        $rootScope.$apply();

        expect(user).not.toBe(null);
        expect(user.userId).toBe(555);
        expect(Session.userId).toBe(user.userId);
    });

    it("should fail when login fails and not be authorized", function () {

        var credentials = {name: 'test', password: 'password'};

        spyOn($gapiMock.client.jasify.auth, 'login')
            .and.returnValue($q.reject());


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

        $rootScope.$apply();

        expect(succeeded).toBe(false);
        expect(failed).toBe(true);
        expect(Auth.isAuthenticated()).toBe(false);

    });

    it("should change password", function () {

        var credentials = {id: 555, name: 'test', password: 'password'};

        spyOn($gapiMock.client.jasify.auth, 'changePassword').and.callThrough();

        var ok = false;

        Auth.changePassword(credentials, 'newPw').then(function () {
            ok = true;
        });

        expect(ok).toBe(false);

        $rootScope.$apply();

        expect(ok).toBe(true);
        expect($gapiMock.client.jasify.auth.changePassword).toHaveBeenCalledWith({
            userId: credentials.id,
            oldPassword: credentials.password,
            newPassword: 'newPw'
        });
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
