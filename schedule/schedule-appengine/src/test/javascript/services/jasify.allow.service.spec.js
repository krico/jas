describe('Allow', function () {
    var $rootScope, Allow, Session, AUTH_EVENTS;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$rootScope_, _Allow_, _Session_, _AUTH_EVENTS_) {
        $rootScope = _$rootScope_;
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
