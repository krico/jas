describe('User', function () {
    var User, $httpBackend, $rootScope;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_User_) {
        User = _User_;
    }));

    beforeEach(inject(function (_$httpBackend_, _$rootScope_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

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
            if (url.indexOf('/user?') !== 0) return false;
            for (var p in q) {
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
