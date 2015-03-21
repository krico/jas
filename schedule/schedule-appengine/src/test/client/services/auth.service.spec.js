describe('AuthService', function () {
    var Session, Auth, BrowserData, Endpoint, $q, $httpBackend, $rootScope, $gapiMock, $location;
    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_BrowserData_, _$location_, _Session_, _Auth_, _Endpoint_, _$q_, _$httpBackend_, _$rootScope_, _$gapiMock_) {
        BrowserData = _BrowserData_;
        $location = _$location_;
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

    it("should call call Auth backend for providerAuthorize", function () {
        var url = "http://jasify.cool/path/to#!/Whatever";
        spyOn($location, 'absUrl').and.returnValue(url);
        spyOn($location, 'path').and.returnValue("/Whatever");
        spyOn($gapiMock.client.jasify.auth, 'providerAuthorize').and.returnValue($q.when({result: 'http://go'}));

        var provider = "Provide";
        Auth.providerAuthorize(provider);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.providerAuthorize).toHaveBeenCalledWith({
            provider: provider,
            data: "/Whatever",
            baseUrl: 'http://jasify.cool/path/to'
        });

    });

    it("should call call Auth backend for providerAuthorize with proper page.html", function () {
        var url = "http://jasify.cool/book-it.html#/Whatever";
        spyOn($location, 'absUrl').and.returnValue(url);
        spyOn($location, 'path').and.returnValue("/Whatever");
        spyOn($gapiMock.client.jasify.auth, 'providerAuthorize').and.returnValue($q.when({result: 'http://go'}));

        var provider = "Provide";
        Auth.providerAuthorize(provider);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.providerAuthorize).toHaveBeenCalledWith({
            provider: provider,
            data: '/Whatever',
            baseUrl: 'http://jasify.cool/book-it.html'
        });

    });

    it("should call call Auth backend for providerAuthorize with no path", function () {
        var url = "http://jasify.cool/";
        spyOn($location, 'absUrl').and.returnValue(url);
        spyOn($location, 'path').and.returnValue("/");
        spyOn($gapiMock.client.jasify.auth, 'providerAuthorize').and.returnValue($q.when({result: 'http://go'}));

        var provider = "Provide";
        Auth.providerAuthorize(provider);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.providerAuthorize).toHaveBeenCalledWith({
            provider: provider,
            data: '/',
            baseUrl: 'http://jasify.cool/'
        });

    });

    it("should call call Auth backend for callbackUrl", function () {
        spyOn($gapiMock.client.jasify.auth, 'providerAuthenticate').and.returnValue($q.when({result: {}}));
        var url = "http://jasify.cool/";

        Auth.providerAuthenticate(url);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.providerAuthenticate).toHaveBeenCalledWith({
            callbackUrl: url
        });

    });

    it("should restore an existing session", function () {

        $httpBackend.expectGET('/auth/restore')
            .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: 'test'}});

        var user = null;

        BrowserData.setLoggedIn(true);

        Auth.restore().then(function (u) {
            user = u;
        });

        $httpBackend.flush();

        expect(Auth.isAuthenticated()).toBe(true);
        expect(user).not.toBe(null);
        expect(user.id).toBe(555);
        expect(Session.userId).toBe(user.id);
    });

    it("should restore locally if data is provided", function () {

        var restoreData = {id: 'someSessionId', userId: 555, user: {id: 555, name: 'test'}};

        var user = null;

        BrowserData.setLoggedIn(true);

        Auth.restore(restoreData).then(function (u) {
            user = u;
        });

        $rootScope.$apply();

        expect(Auth.isAuthenticated()).toBe(true);
        expect(user).not.toBe(null);
        expect(user.id).toBe(555);
        expect(Session.userId).toBe(user.id);
    });

    it("should detect there is no session to restore", function () {

        $httpBackend.expectGET('/auth/restore').respond(401);

        var user = null;
        var failed = false;

        BrowserData.setLoggedIn(true);

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

        BrowserData.setLoggedIn(true);

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

        BrowserData.setLoggedIn(true);

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

    it('should call jasify.forgotPassword when Auth.forgotPassword is called', function () {
        var url = "http://jasify.cool/";
        spyOn($location, 'absUrl').and.returnValue(url);
        spyOn($gapiMock.client.jasify.auth, 'forgotPassword').and.returnValue($q.when({result: true}));

        var email = "x@com";
        Auth.forgotPassword(email);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.forgotPassword).toHaveBeenCalledWith({
            email: email,
            url: url
        });

    });

    it('should call jasify.recoverPassword when Auth.recoverPassword is called', function () {
        var url = "http://jasify.cool/";
        spyOn($location, 'absUrl').and.returnValue(url);
        spyOn($gapiMock.client.jasify.auth, 'recoverPassword').and.returnValue($q.when({result: true}));

        var code = "1234";
        var pw = "4321";
        Auth.recoverPassword(code, pw);

        $rootScope.$apply();

        expect($gapiMock.client.jasify.auth.recoverPassword).toHaveBeenCalledWith({
            code: code,
            newPassword: pw
        });

    });


});
